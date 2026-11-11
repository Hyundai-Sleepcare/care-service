package com.hyundai.ngmb

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hyundai.ngmb.ui.theme.*
import java.util.Locale

enum class SettingSubTab(val label: String) {
    SEAT("💺 좌석 설정"),
    CLIMATE("💨 공조 설정"),
    LIGHT("☀️ 조명 설정")
}

@Composable
fun SettingScreen(
    currentProfile: SleepProfile,
    initialSubTab: SettingSubTab = SettingSubTab.SEAT,
    onSaveProfilePreset: (SleepProfile) -> Unit,
    onApplyAndRun: (SleepProfile) -> Unit
) {
    var selectedSubTab by remember(initialSubTab) { mutableStateOf(initialSubTab) }

    // 조절 가능한 상태 변수들 (문자열 파싱 없이 프로필의 실제 숫자/코드를 그대로 사용!)
    var backrestAngle by remember(currentProfile) {
        mutableIntStateOf(currentProfile.backrestAngle)
    }
    var seatHeatLevel by remember(currentProfile) {
        mutableIntStateOf(currentProfile.seatHeatLevel)
    }
    var seatVentLevel by remember(currentProfile) {
        mutableIntStateOf(currentProfile.seatVentLevel)
    }
    var targetTemp by remember(currentProfile) {
        mutableDoubleStateOf(currentProfile.targetCelsius)
    }
    var selectedMood by remember(currentProfile) {
        mutableStateOf(currentProfile.mood)
    }
    var showSavedToast by remember { mutableStateOf(false) }

    // 현재 조절된 숫자/코드 값들로 새로운 SleepProfile 객체를 만드는 헬퍼 함수
    fun buildUpdatedProfile(): SleepProfile {
        return currentProfile.copy(
            backrestAngle = backrestAngle,
            seatHeatLevel = if (seatVentLevel > 0) 0 else seatHeatLevel,
            seatVentLevel = if (seatHeatLevel > 0) 0 else seatVentLevel,
            targetCelsius = targetTemp,
            mood = selectedMood
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. 상단 서브 탭 바 (좌석 설정 / 공조 설정 / 조명 설정) + SDK 연동 뱃지
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(SleepSurface)
                    .border(1.dp, SleepBorder, RoundedCornerShape(24.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                SettingSubTab.entries.forEach { tab ->
                    val isSelected = (tab == selectedSubTab)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) AmberPrimary else SleepSurface)
                            .clickable { selectedSubTab = tab }
                            .padding(horizontal = 18.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.label,
                            color = if (isSelected) TextOnAmber else TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            // 우측 '현대 Pleos In-Vehicle SDK 연동됨' 뱃지
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
                    text = "현대 Pleos In-Vehicle SDK 연동됨",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 2. 본문 영역: 좌측 컨트롤 패널(1.65f) + 우측 실시간 요약 패널(1f)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 좌측 영역: 선택된 서브 탭에 따라 다른 설정 컨트롤 표시
            Column(
                modifier = Modifier
                    .weight(1.65f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedSubTab) {
                    SettingSubTab.SEAT -> {
                        // 상단 카드: 등받이 각도 조절 (100° ~ 145°)
                        BackrestControlCard(
                            angle = backrestAngle,
                            onAngleChange = { newAngle ->
                                backrestAngle = newAngle.coerceIn(100, 145)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1.25f)
                        )

                        // 하단 2개 카드: 열선 시트 & 통풍 시트
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            StepLevelCard(
                                icon = "♨️",
                                title = "열선 시트",
                                subtitle = if (seatHeatLevel == 0) "꺼짐" else "${seatHeatLevel}단계 따뜻함 유지 중",
                                selectedLevel = seatHeatLevel,
                                onLevelSelected = { level ->
                                    seatHeatLevel = level
                                    if (level > 0) seatVentLevel = 0
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )

                            StepLevelCard(
                                icon = "💨",
                                title = "통풍 시트",
                                subtitle = if (seatVentLevel == 0) "꺼짐 (동승자 수면 보호)" else "${seatVentLevel}단계 쾌적 바람",
                                selectedLevel = seatVentLevel,
                                onLevelSelected = { level ->
                                    seatVentLevel = level
                                    if (level > 0) seatHeatLevel = 0
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                        }
                    }

                    SettingSubTab.CLIMATE -> {
                        // 공조 설정 탭: 목표 실내 온도 (16.0 ~ 32.0°C, 0.5°C 단위)
                        ClimateControlCard(
                            targetTemp = targetTemp,
                            onTempChange = { newTemp ->
                                targetTemp = newTemp.coerceIn(16.0, 32.0)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    SettingSubTab.LIGHT -> {
                        // 조명 설정 탭: 무드 조명 선택
                        LightControlCard(
                            selectedMood = selectedMood,
                            onMoodSelected = { selectedMood = it },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // 우측 영역: PLEOS INTEGRATED AMBIANCE 실시간 요약 및 가이드
            PleosAmbianceSummaryCard(
                tempText = String.format(Locale.US, "%.1f°C (무풍 미풍)", targetTemp),
                moodText = "${selectedMood.displayName} 20%",
                angleText = "${backrestAngle}° ${if (backrestAngle >= 125) "릴랙스" else "표준"}",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }

        // 3. 하단 버튼 바 ([현재 조합 저장] / [설정 적용 ✓])
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(SleepSurface)
                    .border(1.dp, SleepBorder, RoundedCornerShape(24.dp))
                    .clickable {
                        onSaveProfilePreset(buildUpdatedProfile())
                        showSavedToast = true
                    }
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (showSavedToast) "✓ '${currentProfile.name}' 프리셋 저장 완료" else "🔖 현재 조합 저장",
                    color = if (showSavedToast) AmberPrimary else TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(AmberPrimary)
                    .clickable { onApplyAndRun(buildUpdatedProfile()) }
                    .padding(horizontal = 36.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "설정 적용 ✓",
                    color = TextOnAmber,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun BackrestControlCard(
    angle: Int,
    onAngleChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = ((angle - 100).toFloat() / 45f).coerceIn(0f, 1f)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SleepSurface)
            .border(1.dp, SleepBorder, RoundedCornerShape(20.dp))
            .padding(22.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 좌측 시트 일러스트 영역
        Column(
            modifier = Modifier
                .width(150.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SleepCardSurface)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(text = "🔒 정차 중 조절 가능", color = TextSecondary, fontSize = 10.sp)
            }

            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(SleepCardSurface)
                    .border(1.dp, AmberPrimary.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "💺", fontSize = 42.sp)
            }
        }

        // 우측 각도 게이지 및 [-2° / +2°] 조절 버튼
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "BACKREST ANGLE",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "등받이 각도 조절",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleepCardSurface)
                        .border(1.dp, SleepBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "${angle}°",
                        color = AmberPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 게이지 바 (기본 100° ~ 최대 릴랙스 145°)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(SleepCardSurface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(5.dp))
                            .background(AmberPrimary)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "기본 100°", color = TextSecondary, fontSize = 11.sp)
                    Text(text = "최대 릴랙스 145°", color = TextSecondary, fontSize = 11.sp)
                }
            }

            // [- 세우기 (-2°)] / [+ 눕히기 (+2°)] 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleepCardSurface)
                        .border(1.dp, SleepBorder, RoundedCornerShape(12.dp))
                        .clickable { onAngleChange(angle - 2) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "— 세우기 (-2°)",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleepCardSurface)
                        .border(1.dp, SleepBorder, RoundedCornerShape(12.dp))
                        .clickable { onAngleChange(angle + 2) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+ 눕히기 (+2°)",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun StepLevelCard(
    icon: String,
    title: String,
    subtitle: String,
    selectedLevel: Int,
    onLevelSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf("OFF", "1단", "2단", "3단")

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SleepSurface)
            .border(1.dp, SleepBorder, RoundedCornerShape(20.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SleepCardSurface),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 16.sp)
            }

            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SleepCardSurface)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            options.forEachIndexed { index, label ->
                val isSelected = (selectedLevel == index)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) AmberPrimary else SleepCardSurface)
                        .clickable { onLevelSelected(index) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) TextOnAmber else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ClimateControlCard(
    targetTemp: Double,
    onTempChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = ((targetTemp - 16.0) / 16.0).toFloat().coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SleepSurface)
            .border(1.dp, SleepBorder, RoundedCornerShape(20.dp))
            .padding(28.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CABIN TEMPERATURE",
                    color = AmberPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "목표 실내 온도 조절 (0.5°C 단위)",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(SleepCardSurface)
                    .border(1.dp, SleepBorder, RoundedCornerShape(14.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = String.format(Locale.US, "%.1f°C", targetTemp),
                    color = AmberPrimary,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(SleepCardSurface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(AmberPrimary)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "최저 16.0°C", color = TextSecondary, fontSize = 12.sp)
                Text(text = "수면 권장 22.0°C ~ 24.0°C", color = AmberPrimary, fontSize = 12.sp)
                Text(text = "최고 32.0°C", color = TextSecondary, fontSize = 12.sp)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SleepCardSurface)
                    .border(1.dp, SleepBorder, RoundedCornerShape(14.dp))
                    .clickable { onTempChange(targetTemp - 0.5) }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "— 온도 낮추기 (-0.5°C)",
                    color = TextPrimary,
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
                    .clickable { onTempChange(targetTemp + 0.5) }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+ 온도 높이기 (+0.5°C)",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun LightControlCard(
    selectedMood: MoodType,
    onMoodSelected: (MoodType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SleepSurface)
            .border(1.dp, SleepBorder, RoundedCornerShape(20.dp))
            .padding(28.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "AMBIENT LIGHT CARE",
                color = AmberPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "슬립 모드 무드 조명 설정",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "수면 진입 시 눈부심을 줄이고 슬립 모드 해제 시 원래 조명 상태로 자동 복귀합니다.",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MoodType.entries.forEach { mood ->
                val isSelected = (mood == selectedMood)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(90.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) SleepCardSelected else SleepCardSurface)
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) AmberPrimary else SleepBorder,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onMoodSelected(mood) }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "☀️ ${mood.displayName}",
                        color = if (isSelected) AmberPrimary else TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PleosAmbianceSummaryCard(
    tempText: String,
    moodText: String,
    angleText: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SleepSurface)
            .border(1.dp, SleepBorder, RoundedCornerShape(20.dp))
            .padding(22.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PLEOS INTEGRATED AMBIANCE",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = "✦", color = AmberPrimary, fontSize = 16.sp)
        }

        // 상단 비주얼 배너 박스
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SleepCardSelected)
                .border(1.dp, SleepBorder, RoundedCornerShape(14.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🛋️", fontSize = 32.sp)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "● 안식처 프리셋 동기화",
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "실시간 감응",
                    color = AmberPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 실시간 3줄 요약 (온도 제어 / 무드 조명 / 시트 각도)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SleepCardSurface)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AmbianceRow(label = "🌡️ 온도 제어", value = tempText, highlight = false)
            AmbianceRow(label = "☀️ 무드 조명", value = moodText, highlight = false)
            AmbianceRow(label = "💺 시트 각도", value = angleText, highlight = true)
        }

        // 하단 동승자 최적 각도 가이드 박스
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SleepCardSurface)
                .border(1.dp, SleepBorder, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(
                text = "ⓘ 동승자 최적 각도 가이드\n125°~130° 구간은 척추 부담을 40% 저감하는 최적 수면 권장 범위입니다.",
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun AmbianceRow(label: String, value: String, highlight: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextSecondary, fontSize = 12.sp)
        Text(
            text = value,
            color = if (highlight) AmberPrimary else TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}