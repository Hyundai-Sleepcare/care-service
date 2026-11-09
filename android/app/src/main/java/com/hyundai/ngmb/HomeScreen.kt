package com.hyundai.ngmb

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hyundai.ngmb.ui.theme.*

@Composable
fun HomeScreen(
    selectedSeat: SeatZone,
    profiles: List<SleepProfile>,
    currentProfile: SleepProfile,
    onSeatSelected: (SeatZone) -> Unit,
    onProfileSelected: (SleepProfile) -> Unit,
    onConfirmPresetClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. 상단 헤더 영역 (CABIN ZONE MATRIX / 케어 대상 좌석 선택)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "CABIN ZONE MATRIX",
                    color = AmberPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "케어 대상 좌석 선택",
                    color = TextPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SleepSurface)
                    .border(1.dp, SleepBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "● ", color = AmberPrimary, fontSize = 10.sp)
                Text(
                    text = "안전 운전 동기화 가동 중",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 2. 본문 좌/우 카드 영역
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            VehicleFloorplanCard(
                selectedSeat = selectedSeat,
                onSeatSelected = onSeatSelected,
                modifier = Modifier
                    .weight(1.15f)
                    .fillMaxHeight()
            )

            TargetPassengerCard(
                selectedSeat = selectedSeat,
                profiles = profiles,
                currentProfile = currentProfile,
                onProfileSelected = onProfileSelected,
                onConfirmPresetClick = onConfirmPresetClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
fun VehicleFloorplanCard(
    selectedSeat: SeatZone,
    onSeatSelected: (SeatZone) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SleepSurface)
            .border(1.dp, SleepBorder, RoundedCornerShape(20.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 카드 상단 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🚘 VEHICLE FLOORPLAN",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SleepCardSurface)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(text = "Touch to Select", color = TextSecondary, fontSize = 11.sp)
            }
        }

        // 가운데 2x2 좌석 그리드 (1열: 운전석/조수석, 2열: 2열 좌측/2열 우측)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1열 (운전석, 조수석)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SeatGridItem(
                    seat = SeatZone.DRIVER,
                    isSelected = (selectedSeat == SeatZone.DRIVER),
                    onClick = { onSeatSelected(SeatZone.DRIVER) },
                    modifier = Modifier.weight(1f)
                )
                SeatGridItem(
                    seat = SeatZone.PASSENGER,
                    isSelected = (selectedSeat == SeatZone.PASSENGER),
                    onClick = { onSeatSelected(SeatZone.PASSENGER) },
                    modifier = Modifier.weight(1f)
                )
            }

            // 2열 (2열 좌측, 2열 우측)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SeatGridItem(
                    seat = SeatZone.REAR_LEFT,
                    isSelected = (selectedSeat == SeatZone.REAR_LEFT),
                    onClick = { onSeatSelected(SeatZone.REAR_LEFT) },
                    modifier = Modifier.weight(1f)
                )
                SeatGridItem(
                    seat = SeatZone.REAR_RIGHT,
                    isSelected = (selectedSeat == SeatZone.REAR_RIGHT),
                    onClick = { onSeatSelected(SeatZone.REAR_RIGHT) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 하단 범례 (선택됨 / 선택 가능 / 비활성)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "● ", color = AmberPrimary, fontSize = 10.sp)
            Text(text = "선택됨     ", color = TextSecondary, fontSize = 12.sp)

            Text(text = "● ", color = TextSecondary, fontSize = 10.sp)
            Text(text = "선택 가능     ", color = TextSecondary, fontSize = 12.sp)

            Text(text = "● ", color = TextMuted, fontSize = 10.sp)
            Text(text = "비활성 (운전자 보호)", color = TextMuted, fontSize = 12.sp)
        }
    }
}

@Composable
fun SeatGridItem(
    seat: SeatZone,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDriver = !seat.isSelectable

    Column(
        modifier = modifier
            .height(145.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                when {
                    isSelected -> SleepCardSelected
                    isDriver -> SleepBackground.copy(alpha = 0.7f)
                    else -> SleepCardSurface
                }
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) AmberPrimary else SleepBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(enabled = seat.isSelectable) { onClick() }
            .padding(18.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 상단: 좌석 아이콘 + 우측 상태 표시(선택됨 뱃지 / 자물쇠 / 점)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) AmberPrimary else SleepSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💺",
                    fontSize = 16.sp,
                    color = if (isSelected) TextOnAmber else TextSecondary
                )
            }

            when {
                isDriver -> Text(text = "🔒", fontSize = 14.sp, color = TextMuted)
                isSelected -> {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(AmberPrimary)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "● 선택됨",
                            color = TextOnAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                else -> Text(text = "●", fontSize = 10.sp, color = TextSecondary)
            }
        }

        // 하단: 좌석 이름 + 상태 문구
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = seat.label,
                color = if (isDriver) TextMuted else TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = when {
                    isDriver -> "● 슬립 케어 불가 (운전자)"
                    isSelected -> "슬립 대기 중"
                    else -> "선택 가능"
                },
                color = if (isDriver) TextMuted else TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun TargetPassengerCard(
    selectedSeat: SeatZone,
    profiles: List<SleepProfile>,
    currentProfile: SleepProfile,
    onProfileSelected: (SleepProfile) -> Unit,
    onConfirmPresetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SleepSurface)
            .border(1.dp, SleepBorder, RoundedCornerShape(20.dp))
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 상단: TARGET PASSENGER 헤더 + 선택된 좌석 이름
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💺 TARGET PASSENGER",
                    color = AmberPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleepCardSurface)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = "연동 준비 완료", color = TextSecondary, fontSize = 11.sp)
                }
            }

            Text(
                text = selectedSeat.label,
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "도어 차광막 및 시트 릴렉스 모듈이 동기화됩니다.",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        // 가운데: 최근 적용 프리셋 요약 박스 (홈에서 바로 프로필 전환 가능)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SleepCardSurface)
                .border(1.dp, SleepBorder, RoundedCornerShape(16.dp))
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "최근 적용 프리셋 (터치하여 변경)", color = TextSecondary, fontSize = 12.sp)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AmberHighlight)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "1-Touch Ready",
                        color = AmberPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 홈 화면에서 바로 프로필(아내 / 첫째아들 / 둘째딸)을 고를 수 있는 칩 버튼들
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                profiles.forEach { profile ->
                    val isSelected = (profile.profileId == currentProfile.profileId)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (isSelected) AmberPrimary else SleepSurface)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) AmberPrimary else SleepBorder,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .clickable { onProfileSelected(profile) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${profile.avatarEmoji} ${profile.name}",
                            color = if (isSelected) TextOnAmber else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentProfile.name,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currentProfile.profileTag,
                    color = AmberPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // 선택된 프로필에 따라 바뀌는 3개 요약 칩 (열선 / 온도 / 무드등)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PresetSummaryChip(
                    icon = "♨️",
                    label = currentProfile.seatHeatSummary,
                    modifier = Modifier.weight(1f)
                )
                PresetSummaryChip(
                    icon = "🌡️",
                    label = currentProfile.tempSummary,
                    modifier = Modifier.weight(1f)
                )
                PresetSummaryChip(
                    icon = "☀️",
                    label = currentProfile.moodSummary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 하단: 부가 기능 안내 + [슬립 프리셋 확인] 버튼
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "🔇 헤드레스트 소프트 사운드 활성화", color = TextSecondary, fontSize = 12.sp)
                Text(text = "🌙 눈부심 차단 모드", color = TextSecondary, fontSize = 12.sp)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AmberPrimary)
                    .clickable { onConfirmPresetClick() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎚️ 슬립 프리셋 확인",
                    color = TextOnAmber,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "좌석 변경 시 즉시 개별 맞춤 프리셋으로 갱신됩니다.",
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun PresetSummaryChip(
    icon: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SleepSurface)
            .border(1.dp, SleepBorder, RoundedCornerShape(10.dp))
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = icon, color = AmberPrimary, fontSize = 14.sp)
        Text(text = label, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}