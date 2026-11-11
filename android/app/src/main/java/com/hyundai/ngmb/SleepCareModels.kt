package com.hyundai.ngmb

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

// 메인 화면 상단 네비게이션 바 탭
enum class SleepCareTab(val title: String) {
    HOME("홈"),
    PRESET("프리셋"),
    RUNNING("실행"),
    SETTING("설정"),
    MANAGE("관리")
}

// 차량 내 각 좌석 정보
enum class SeatZone(
    val label: String,
    val description: String,
    val isSelectable: Boolean
) {
    DRIVER(
        label = "운전석",
        description = "슬립 케어 제어 불가 (주행 상태 집중 모드)",
        isSelectable = false
    ),
    PASSENGER(
        label = "조수석",
        description = "도어 차광막 및 시트 릴렉스 모듈이 동기화됩니다.",
        isSelectable = true
    ),
    REAR_LEFT(
        label = "2열 좌측",
        description = "표준 착석 상태 · 프리셋 선택 가능",
        isSelectable = true
    ),
    REAR_RIGHT(
        label = "2열 우측",
        description = "표준 착석 상태 · 프리셋 선택 가능",
        isSelectable = true
    )
}

// 서버 Mood enum (AMBER, SOFT_BLUE, LAVENDER, OFF) 과 1:1 매핑
enum class MoodType(val apiCode: String, val displayName: String) {
    AMBER("AMBER", "앰버 무드"),
    SOFT_BLUE("SOFT_BLUE", "소프트블루"),
    LAVENDER("LAVENDER", "라벤더 무드"),
    OFF("OFF", "무드등 OFF");

    companion object {
        fun fromApiCode(code: String): MoodType {
            return entries.find { it.apiCode == code } ?: AMBER
        }
    }
}

// 사람별 슬립 프리셋 프로필 데이터 모델 (숫자/코드 원본 보관)
data class SleepProfile(
    val profileId: Long,
    val name: String,
    val backrestAngle: Int,       // 100 ~ 145 (정수)
    val seatHeatLevel: Int,       // 0 ~ 3 (정수, 통풍과 동시 켜기 불가)
    val seatVentLevel: Int,       // 0 ~ 3 (정수, 열선과 동시 켜기 불가)
    val targetCelsius: Double,    // 16.0 ~ 32.0 (0.5 단위)
    val mood: MoodType,           // AMBER, SOFT_BLUE, LAVENDER, OFF
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    // 서버 API에 없는 아바타 이모지는 이름/ID에 따라 앱에서 자동 생성
    val avatarEmoji: String
        get() = when {
            name.contains("아내") || name.contains("엄마") || name.contains("어머니") -> "👩"
            name.contains("남편") || name.contains("아빠") || name.contains("아버지") -> "👨"
            name.contains("아들") -> "👦"
            name.contains("딸") -> "👧"
            else -> {
                val emojis = listOf("🙂", "😊", "🌙", "⭐", "🍀")
                emojis[(profileId.toInt().coerceAtLeast(0)) % emojis.size]
            }
        }

    // 서버 API에 없는 프로필 태그도 설정값에 따라 앱에서 자동 생성
    val profileTag: String
        get() = when {
            backrestAngle >= 125 -> "딥슬립 릴랙스 프로필"
            seatVentLevel > 0 -> "표준 쾌적 수면 프로필"
            else -> "포근한 휴식 프로필"
        }

    // 화면 카드 표시용 요약 문자열 (실제 숫자에서 자동 계산됨!)
    val backrestSummary: String
        get() = when {
            backrestAngle >= 125 -> "등받이 릴랙스 (${backrestAngle}°)"
            backrestAngle >= 115 -> "등받이 중간 (${backrestAngle}°)"
            else -> "등받이 표준 (${backrestAngle}°)"
        }

    val seatHeatSummary: String
        get() = when {
            seatHeatLevel > 0 -> "열선 ${seatHeatLevel}단계"
            seatVentLevel > 0 -> "통풍 ${seatVentLevel}단계"
            else -> "열선/통풍 OFF"
        }

    val tempSummary: String
        get() = String.format(Locale.US, "%.1f°C", targetCelsius)

    val moodSummary: String
        get() = mood.displayName

    // 서버 UTC 시각(Instant.parse)을 한국 시각(KST) 문자열로 변환
    val updatedAtKoreaText: String?
        get() = updatedAt?.let { raw ->
            runCatching {
                val instant = Instant.parse(raw)
                val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
                    .withZone(ZoneId.of("Asia/Seoul"))
                formatter.format(instant)
            }.getOrNull()
        }
}

// 오프라인/초기 미리보기용 기본 프로필 목록
val defaultProfiles = listOf(
    SleepProfile(
        profileId = 1L,
        name = "아내",
        backrestAngle = 128,
        seatHeatLevel = 2,
        seatVentLevel = 0,
        targetCelsius = 23.0,
        mood = MoodType.AMBER
    ),
    SleepProfile(
        profileId = 2L,
        name = "첫째아들",
        backrestAngle = 110,
        seatHeatLevel = 0,
        seatVentLevel = 1,
        targetCelsius = 21.5,
        mood = MoodType.SOFT_BLUE
    ),
    SleepProfile(
        profileId = 3L,
        name = "둘째딸",
        backrestAngle = 128,
        seatHeatLevel = 1,
        seatVentLevel = 0,
        targetCelsius = 24.0,
        mood = MoodType.LAVENDER
    )
)