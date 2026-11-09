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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hyundai.ngmb.ui.theme.*

@Composable
fun TopNavBar(
    currentTab: SleepCareTab,
    selectedSeat: SeatZone,
    onTabSelected: (SleepCareTab) -> Unit,
    onSeatButtonClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SleepSurface)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. 왼쪽 영역: 앱 타이틀
        Text(
            text = "🌙 동승자 슬립케어 모드",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        // 2. 오른쪽 영역: 5개 탭 + 좌석 선택 버튼 + 프로필 아이콘
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SleepCareTab.entries.forEach { tab ->
                val isSelected = (tab == currentTab)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) AmberPrimary else SleepSurface)
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.title,
                        color = if (isSelected) TextOnAmber else TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 좌석 선택 버튼 (클릭 시 팝업 열기/닫기)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SleepCardSurface)
                    .border(1.dp, SleepBorder, RoundedCornerShape(20.dp))
                    .clickable { onSeatButtonClick() }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "● ", color = AmberPrimary, fontSize = 10.sp)
                Text(
                    text = "💺 ${selectedSeat.label}",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // 우측 끝 동그란 사용자 아이콘
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(AmberPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "👤", fontSize = 14.sp)
            }
        }
    }
}

// 우측 상단에 뜨는 '제어 좌석 변경' 팝업 오버레이  ( 피그마 Screen 04 )
@Composable
fun SeatSelectionPopover(
    selectedSeat: SeatZone,
    onSeatSelected: (SeatZone) -> Unit,
    onGoToHomeFloorplan: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable { onDismiss() }
            .padding(top = 8.dp, end = 24.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Column(
            modifier = Modifier
                .width(340.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(SleepSurface)
                .border(1.dp, SleepBorder, RoundedCornerShape(18.dp))
                .clickable(enabled = false) {}
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. 팝오버 헤더 + '차량 4인 독립 케어' 알약 뱃지
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "💺 ", fontSize = 14.sp)
                    Text(
                        text = "제어 좌석 변경",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleepCardSurface)
                        .border(1.dp, SleepBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "차량 4인 독립 케어",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            // 2. 4개 좌석 카드 리스트
            SeatZone.entries.forEach { seat ->
                val isCurrent = (seat == selectedSeat)
                val isDriver = !seat.isSelectable
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            when {
                                isCurrent -> SleepCardSelected
                                isDriver -> SleepBackground.copy(alpha = 0.6f)
                                else -> SleepCardSurface
                            }
                        )
                        .border(
                            width = if (isCurrent) 1.5.dp else 1.dp,
                            color = if (isCurrent) AmberPrimary else SleepBorder,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable(enabled = seat.isSelectable) {
                            onSeatSelected(seat)
                            onDismiss()
                        }
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 왼쪽 네모 아이콘 박스
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when {
                                    isCurrent -> AmberPrimary
                                    else -> SleepSurface
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = if (isCurrent) AmberPrimary else SleepBorder,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isDriver) "🔒" else "💺",
                            fontSize = 16.sp,
                            color = if (isCurrent) TextOnAmber else TextSecondary
                        )
                    }
                    // 가운데 좌석명 + 상태 뱃지 + 설명
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = seat.label,
                                color = if (isDriver) TextMuted else TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            // 상태 뱃지 (주행 안전 제한 / 현재 선택됨 / 대기)
                            val badgeBg = when {
                                isDriver -> Color(0xFF3B1D1D)
                                isCurrent -> AmberPrimary
                                else -> SleepSurface
                            }
                            val badgeTextColor = when {
                                isDriver -> Color(0xFFE57373)
                                isCurrent -> TextOnAmber
                                else -> TextSecondary
                            }
                            val badgeText = when {
                                isDriver -> "주행 안전 제한"
                                isCurrent -> "현재 선택됨"
                                else -> "대기"
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(badgeBg)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = badgeText,
                                    color = badgeTextColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isCurrent) "슬립 모드 대기 중 · 128° 릴랙스 무중력" else seat.description,
                            color = if (isDriver) TextMuted else TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                    // 오른쪽 상태 아이콘 (금지 / 앰버 체크 원 / 화살표)
                    when {
                        isDriver -> {
                            Text(text = "⊘", color = TextMuted, fontSize = 16.sp)
                        }
                        isCurrent -> {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(AmberPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "✓",
                                    color = TextOnAmber,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        else -> {
                            Text(text = "›", color = TextSecondary, fontSize = 18.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            // 3. 하단 '전체 좌석 배치도 보기 (홈) ->' 버튼
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SleepCardSurface)
                    .border(1.dp, SleepBorder, RoundedCornerShape(12.dp))
                    .clickable {
                        onGoToHomeFloorplan()
                        onDismiss()
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⊞ 전체 좌석 배치도 보기 (홈) →",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}