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