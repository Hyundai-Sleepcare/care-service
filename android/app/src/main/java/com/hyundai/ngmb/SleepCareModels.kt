package com.hyundai.ngmb

import kotlinx.serialization.descriptors.PrimitiveKind


// 메인 화면 상단 네이게이션 바 탭
enum class SleepCareTab(val title: String) {
    HOME(" 홈"),
    PRESET("프리셋"),
    RUNNING("실행"),
    SETTING("설정"),
    MANAGE("관리")
}

// 차량 내 각 좌석 정보
enum class SeatZone (
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

// 사람별 슬립 프리셋 프로필 데이터 모델
data class SleepProfile(
    val profileId: Int,
    val name: String,
    val avatarEmoji: String,
    val profileTag: String,
    val backrestSummary: String,
    val seatHeatSummary: String,
    val tempSummary: String,
    val moodSummary: String
)
// 기본 가족 프로필 목록 (나중에 서버 API GET /api/v1/profiles 연동 시 교체될 데이터)
val defaultProfiles = listOf(
    SleepProfile(
        profileId = 1,
        name = "아내",
        avatarEmoji = "👩",
        profileTag = "35분 딥슬립 프로필",
        backrestSummary = "등받이 릴랙스",
        seatHeatSummary = "열선 2단계",
        tempSummary = "23.0°C",
        moodSummary = "앰버 무드"
    ),
    SleepProfile(
        profileId = 2,
        name = "첫째아들",
        avatarEmoji = "👦",
        profileTag = "표준 쾌적 수면 프로필",
        backrestSummary = "등받이 표준",
        seatHeatSummary = "통풍 1단계",
        tempSummary = "21.5°C",
        moodSummary = "소프트블루"
    ),
    SleepProfile(
        profileId = 3,
        name = "둘째딸",
        avatarEmoji = "👧",
        profileTag = "포근한 휴식 프로필",
        backrestSummary = "등받이 릴랙스",
        seatHeatSummary = "열선 1단계",
        tempSummary = "24.0°C",
        moodSummary = "라벤더 무드"
    )
)