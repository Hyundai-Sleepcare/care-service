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
    runningSeats: Set<SeatZone>,
    profiles: List<SleepProfile>,
    currentProfile: SleepProfile,
    onSeatSelected: (SeatZone) -> Unit,
    onProfileSelected: (SleepProfile) -> Unit,
    onConfirmPresetClick: () -> Unit,
    onGoToRunningClick: () -> Unit,
    onStartSleepClick: () -> Unit,
    onStopSleepClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. 상단 헤더 영역
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
                    text = if (runningSeats.isNotEmpty()) {
                        "현재 ${runningSeats.size}개 좌석 슬립 케어 작동 중"
                    } else {
                        "안전 운전 동기화 가동 중"
                    },
                    color = if (runningSeats.isNotEmpty()) AmberPrimary else TextSecondary,
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
                runningSeats = runningSeats,
                onSeatSelected = onSeatSelected,
                modifier = Modifier
                    .weight(1.15f)
                    .fillMaxHeight()
            )

            TargetPassengerCard(
                selectedSeat = selectedSeat,
                isRunning = (selectedSeat in runningSeats),
                profiles = profiles,
                currentProfile = currentProfile,
                onProfileSelected = onProfileSelected,
                onConfirmPresetClick = onConfirmPresetClick,
                onGoToRunningClick = onGoToRunningClick,
                onStartSleepClick = onStartSleepClick,
                onStopSleepClick = onStopSleepClick,
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
    runningSeats: Set<SeatZone>,
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
                    isRunning = (SeatZone.DRIVER in runningSeats),
                    onClick = { onSeatSelected(SeatZone.DRIVER) },
                    modifier = Modifier.weight(1f)
                )
                SeatGridItem(
                    seat = SeatZone.PASSENGER,
                    isSelected = (selectedSeat == SeatZone.PASSENGER),
                    isRunning = (SeatZone.PASSENGER in runningSeats),
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
                    isRunning = (SeatZone.REAR_LEFT in runningSeats),
                    onClick = { onSeatSelected(SeatZone.REAR_LEFT) },
                    modifier = Modifier.weight(1f)
                )
                SeatGridItem(
                    seat = SeatZone.REAR_RIGHT,
                    isSelected = (selectedSeat == SeatZone.REAR_RIGHT),
                    isRunning = (SeatZone.REAR_RIGHT in runningSeats),
                    onClick = { onSeatSelected(SeatZone.REAR_RIGHT) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "● ", color = AmberPrimary, fontSize = 10.sp)
            Text(text = "선택됨 / 작동 중     ", color = TextSecondary, fontSize = 12.sp)

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
    isRunning: Boolean,
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
                width = if (isSelected || isRunning) 1.5.dp else 1.dp,
                color = if (isSelected || isRunning) AmberPrimary else SleepBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(enabled = seat.isSelectable) { onClick() }
            .padding(18.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
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
                    text = if (isRunning) "🌙" else "💺",
                    fontSize = 16.sp,
                    color = if (isSelected) TextOnAmber else TextSecondary
                )
            }

            when {
                isDriver -> Text(text = "🔒", fontSize = 14.sp, color = TextMuted)
                isRunning -> {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(AmberHighlight)
                            .border(1.dp, AmberPrimary, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "🌙 작동 중",
                            color = AmberPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
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
                    isRunning -> "🌙 슬립 케어 작동 중"
                    isSelected -> "슬립 대기 중"
                    else -> "선택 가능"
                },
                color = when {
                    isDriver -> TextMuted
                    isRunning -> AmberPrimary
                    else -> TextSecondary
                },
                fontSize = 12.sp,
                fontWeight = if (isRunning) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun TargetPassengerCard(
    selectedSeat: SeatZone,
    isRunning: Boolean,
    profiles: List<SleepProfile>,
    currentProfile: SleepProfile,
    onProfileSelected: (SleepProfile) -> Unit,
    onConfirmPresetClick: () -> Unit,
    onGoToRunningClick: () -> Unit,
    onStartSleepClick: () -> Unit,
    onStopSleepClick: () -> Unit,
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
                        .background(if (isRunning) AmberHighlight else SleepCardSurface)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isRunning) "🌙 슬립 모드 작동 중" else "연동 준비 완료",
                        color = if (isRunning) AmberPrimary else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isRunning) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Text(
                text = selectedSeat.label,
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isRunning) {
                    "'${currentProfile.name}' 프리셋으로 슬립 케어가 작동 중입니다."
                } else {
                    "도어 차광막 및 시트 릴렉스 모듈이 동기화됩니다."
                },
                color = if (isRunning) AmberPrimary else TextSecondary,
                fontSize = 13.sp
            )
        }

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
                Text(
                    text = if (isRunning) "현재 적용 중인 프리셋" else "최근 적용 프리셋 (터치하여 변경)",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AmberHighlight)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isRunning) "ACTIVE" else "1-Touch Ready",
                        color = AmberPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

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

            if (isRunning) {
                // 슬립 모드가 작동 중일 때: [실행 화면 보기] + [프리셋 확인]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(AmberPrimary)
                            .clickable { onGoToRunningClick() }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🌙 실행 화면 보기",
                            color = TextOnAmber,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(DangerRed)
                            .clickable { onStopSleepClick() }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "⏻ 슬립 종료",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // 대기 상태일 때: 홈에서 바로 켜는 [즉시 슬립 시작] + [프리셋 상세]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(AmberPrimary)
                            .clickable { onStartSleepClick() }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🌙 즉시 슬립 시작",
                            color = TextOnAmber,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(SleepCardSurface)
                            .border(1.dp, SleepBorder, RoundedCornerShape(14.dp))
                            .clickable { onConfirmPresetClick() }
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🎚️ 프리셋 상세",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
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