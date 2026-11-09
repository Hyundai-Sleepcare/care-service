package com.hyundai.ngmb

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hyundai.ngmb.ui.theme.*

@Composable
fun RunningScreen(
    selectedSeat: SeatZone,
    currentProfile: SleepProfile,
    onAdjustClick: (SettingSubTab) -> Unit,
    onExitSleepMode: (restorePrevious: Boolean) -> Unit
) {
    // [슬립 모드 종료] 버튼 클릭 시 종료 확인 모달을 띄울지 여부
    var showExitModal by remember { mutableStateOf(false) }
    // [현재 조합 저장] 클릭 시 안내 문구 표시 여부
    var showSavedBanner by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. 상단 작동 중 배너 (조수석 슬립 모드 작동 중)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SleepCardSelected)
                    .border(1.dp, SleepBorder, RoundedCornerShape(18.dp))
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AmberHighlight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🌙", fontSize = 22.sp)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "● ${selectedSeat.label} 전용 케어 / '${currentProfile.name}' 프리셋 적용 중",
                            color = AmberPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${selectedSeat.label} 슬립 모드 작동 중",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 우측 상태 뱃지 2개 (도어 커튼 닫힘 / 알림 음소거)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatusBadgeChip(text = "🛡️ 도어 커튼 닫힘")
                    StatusBadgeChip(text = "🔇 알림 음소거")
                }
            }

            // 2. 가운데 3개 작동 상태 카드 (좌석 / 공조 / 조명)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                RunningStatusCard(
                    icon = "💺",
                    badgeText = "릴렉스 포지션",
                    category = "좌석",
                    statusValue = "${currentProfile.backrestSummary} · ${currentProfile.seatHeatSummary}",
                    onAdjustClick = { onAdjustClick(SettingSubTab.SEAT) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

                RunningStatusCard(
                    icon = "🌡️",
                    badgeText = "저소음 집중",
                    category = "공조",
                    statusValue = "${currentProfile.tempSummary} · 풍량 2단계",
                    onAdjustClick = { onAdjustClick(SettingSubTab.CLIMATE) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )

                RunningStatusCard(
                    icon = "💡",
                    badgeText = "멜라토닌 보호",
                    category = "조명",
                    statusValue = "${currentProfile.moodSummary} 30% · 독서등 OFF",
                    onAdjustClick = { onAdjustClick(SettingSubTab.LIGHT) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }

            // 3. 하단 버튼 바 ([현재 조합 저장] / [슬립 모드 종료])
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 좌측: 현재 조합 저장 버튼
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(SleepSurface)
                        .border(1.dp, SleepBorder, RoundedCornerShape(24.dp))
                        .clickable { showSavedBanner = true }
                        .padding(horizontal = 24.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (showSavedBanner) "✓ '${currentProfile.name}' 프리셋에 저장됨" else "🔖 현재 조합 저장",
                        color = if (showSavedBanner) AmberPrimary else TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 우측: 빨간색 슬립 모드 종료 버튼
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(DangerRed)
                        .clickable { showExitModal = true }
                        .padding(horizontal = 28.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⏻ 슬립 모드 종료",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 4. [슬립 모드 종료] 클릭 시 뜨는 모달 (피그마 Screen 05 팝업 디자인)
        if (showExitModal) {
            SleepModeExitModal(
                selectedSeat = selectedSeat,
                currentProfile = currentProfile,
                onRestoreAndExit = {
                    showExitModal = false
                    onExitSleepMode(true)
                },
                onKeepAndExit = {
                    showExitModal = false
                    onExitSleepMode(false)
                },
                onCancel = { showExitModal = false }
            )
        }
    }
}

@Composable
fun StatusBadgeChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SleepSurface)
            .border(1.dp, SleepBorder, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun RunningStatusCard(
    icon: String,
    badgeText: String,
    category: String,
    statusValue: String,
    onAdjustClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SleepSurface)
            .border(1.dp, SleepBorder, RoundedCornerShape(20.dp))
            .clickable { onAdjustClick() }
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 카드 상단: 좌측 아이콘 박스 + 우측 뱃지
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SleepCardSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 20.sp)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SleepCardSurface)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(text = badgeText, color = TextSecondary, fontSize = 11.sp)
            }
        }

        // 카드 중앙: 카테고리 + 현재 작동 상태
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = category,
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = statusValue,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 카드 하단: '조절하기 >' + 앰버 작동 표시 점
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "조절하기 ›",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Text(text = "●", color = AmberPrimary, fontSize = 12.sp)
        }
    }
}

// 피그마 Screen 05의 '슬립 모드를 종료할까요?' 모달
@Composable
fun SleepModeExitModal(
    selectedSeat: SeatZone,
    currentProfile: SleepProfile,
    onRestoreAndExit: () -> Unit,
    onKeepAndExit: () -> Unit,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onCancel() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(680.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(SleepSurface)
                .border(1.dp, SleepBorder, RoundedCornerShape(24.dp))
                .clickable(enabled = false) {}
                .padding(28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 모달 상단 헤더
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SleepCardSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⏻", color = AmberPrimary, fontSize = 20.sp)
                    }

                    Column {
                        Text(
                            text = "SLEEP CARE MODE EXIT ●",
                            color = AmberPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "슬립 모드를 종료할까요?",
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SleepCardSurface)
                        .clickable { onCancel() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✕", color = TextSecondary, fontSize = 14.sp)
                }
            }

            // 두 가지 종료 옵션 카드 (이전 설정 복원 vs 현재 설정 유지)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 옵션 1: 이전 설정 복원 (추천 옵션)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(SleepCardSelected)
                        .border(1.5.dp, AmberPrimary, RoundedCornerShape(18.dp))
                        .clickable { onRestoreAndExit() }
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SleepSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "↺", color = AmberPrimary, fontSize = 18.sp)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AmberPrimary)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "추천 옵션",
                                color = TextOnAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "이전 설정 복원",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "슬립 진입 전 상태로 원상 복귀",
                        color = AmberPrimary,
                        fontSize = 12.sp
                    )

                    Text(
                        text = "✓ 공조 온도 정상 모드 복귀\n✓ 시트 열선 및 무드 조명 원래 상태 복귀",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SleepBackground.copy(alpha = 0.6f))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "ⓘ 등받이 각도는 주행 안전을 위해 정차 후 별도 복원됩니다.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // 옵션 2: 현재 설정 유지
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(18.dp))
                        .background(SleepCardSurface)
                        .border(1.dp, SleepBorder, RoundedCornerShape(18.dp))
                        .clickable { onKeepAndExit() }
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SleepSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🛋️", fontSize = 16.sp)
                    }

                    Text(
                        text = "현재 설정 유지",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "현재의 편안한 상태 그대로 유지",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Text(
                        text = "현재 조절된 시트 각도, 온도(${currentProfile.tempSummary}) 및 앰비언트 라이트 조도를 그대로 유지한 채 슬립 모드만 종료합니다.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SleepBackground.copy(alpha = 0.6f))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "🌙 수면 타이머 및 모니터링만 비활성화",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // 모달 하단 바 (안전 규정 안내 + 취소 버튼)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🚘 ${selectedSeat.label} 제어 화면 · 안전 규정 준수 작동 중",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SleepCardSurface)
                        .border(1.dp, SleepBorder, RoundedCornerShape(20.dp))
                        .clickable { onCancel() }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "취소 (계속 슬립 유지)",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}