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
fun PresetScreen(
    selectedSeat: SeatZone,
    profiles: List<SleepProfile>,
    currentProfile: SleepProfile,
    onProfileSelected: (SleepProfile) -> Unit,
    onCustomSettingClick: () -> Unit,
    onStartSleepModeClick: () -> Unit,
    onAddPresetClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. 상단 타이틀 영역 (CO-DRIVER SEAT CARE / 누구의 슬립 설정을 적용할까요?)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "● CO-DRIVER SEAT CARE (${selectedSeat.label})",
                    color = AmberPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "누구의 슬립 설정을 적용할까요?",
                    color = TextPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 우측 '동승석 메모리 프로필' 뱃지
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SleepSurface)
                    .border(1.dp, SleepBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💺 동승석 메모리 프로필",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 2. 가운데 4개 카드 영역 (프로필 카드 3개 + 새 프리셋 추가 카드 1개)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            profiles.forEach { profile ->
                PresetProfileCard(
                    profile = profile,
                    isSelected = (profile.profileId == currentProfile.profileId),
                    onClick = { onProfileSelected(profile) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }

            // 4번째 카드: [+ 새 프리셋 추가]
            AddPresetCard(
                onClick = onAddPresetClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }

        // 3. 중간 안내 배너 (안락한 휴식 환경 동기화)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SleepSurface)
                .border(1.dp, SleepBorder, RoundedCornerShape(16.dp))
                .padding(horizontal = 22.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SleepCardSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🌙", fontSize = 16.sp)
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "안락한 휴식 환경 동기화",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "선택된 프리셋에 따라 조명 밝기와 시트가 부드럽게 전환됩니다",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            // 우측 파형 장식 아이콘
            Text(
                text = "ılılı",
                color = AmberPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 4. 하단 버튼 바 ([직접 설정하기] / [OO 프리셋으로 슬립 모드 시작])
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 좌측: 직접 설정하기 버튼
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(SleepSurface)
                    .border(1.dp, SleepBorder, RoundedCornerShape(24.dp))
                    .clickable { onCustomSettingClick() }
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🎚️ 직접 설정하기",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 우측: 선택된 프로필로 슬립 모드 시작 버튼
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(AmberPrimary)
                    .clickable { onStartSleepModeClick() }
                    .padding(horizontal = 28.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🌙 ${currentProfile.name} 프리셋으로 슬립 모드 시작",
                    color = TextOnAmber,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PresetProfileCard(
    profile: SleepProfile,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (isSelected) SleepCardSelected else SleepSurface)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) AmberPrimary else SleepBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 카드 상단: 아바타 원형 아이콘 + '최근 사용' 뱃지
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) AmberHighlight else SleepCardSurface)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) AmberPrimary else SleepBorder,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = profile.avatarEmoji, fontSize = 20.sp)
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(AmberHighlight)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "● 최근 사용",
                        color = AmberPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 프로필 이름 (아내 / 첫째아들 / 둘째딸)
        Text(
            text = profile.name,
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        // 3줄 설정 요약 (등받이 / 열선 / 무드등·온도)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PresetDetailRow(icon = "🛋️", text = profile.backrestSummary)
            PresetDetailRow(icon = "♨️", text = profile.seatHeatSummary)
            PresetDetailRow(icon = "☀️", text = "무드등 ${profile.moodSummary} · ${profile.tempSummary}")
        }
    }
}

@Composable
fun PresetDetailRow(icon: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SleepBackground.copy(alpha = 0.55f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = icon, fontSize = 12.sp)
        Text(
            text = text,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun AddPresetCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(SleepSurface)
            .border(1.dp, SleepBorder, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(SleepCardSurface)
                .border(1.dp, SleepBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "+", color = TextPrimary, fontSize = 26.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "새 프리셋 추가",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "현재 좌석 설정 저장",
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}