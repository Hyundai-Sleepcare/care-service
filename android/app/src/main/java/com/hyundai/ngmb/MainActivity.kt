package com.hyundai.ngmb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.SemanticsProperties.Selected
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hyundai.ngmb.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NGMBTheme {
                SleepCareApp()
            }
        }
    }
}

@Composable
fun SleepCareApp() {
    var currentTab by remember { mutableStateOf(SleepCareTab.HOME) }
    var selectedSeat by remember { mutableStateOf(SeatZone.PASSENGER) }
    var isSeatMenuOpen by remember { mutableStateOf(false) }
    var selectedSettingSubTab by remember { mutableStateOf(SettingSubTab.SEAT) }
    var runningSeats by remember { mutableStateOf(setOf<SeatZone>()) }

    // 전체 프로필 목록 상태 (서버 연결 시 빈 목록 emptyList()가 들어올 수도 있음)
    var profiles by remember { mutableStateOf(defaultProfiles) }

    // 고정 ID(1, 2, 3)를 쓰지 않고 빈 맵으로 시작! 사용자가 선택한 profileId(Long)만 저장
    var seatProfileIdMap by remember { mutableStateOf(emptyMap<SeatZone, Long>()) }

    // 좌석별 기본 프로필 매칭 (고정 ID 없이 현재 목록 순서대로 안전하게 꺼내옴, 빈 목록이면 null!)
    val defaultIndexForSeat = when (selectedSeat) {
        SeatZone.PASSENGER -> 0
        SeatZone.REAR_LEFT -> 1
        SeatZone.REAR_RIGHT -> 2
        SeatZone.DRIVER -> 0
    }
    val savedProfileId = seatProfileIdMap[selectedSeat]
    val currentProfile: SleepProfile? = savedProfileId?.let { id ->
        profiles.find { it.profileId == id }
    } ?: profiles.getOrNull(defaultIndexForSeat) ?: profiles.firstOrNull()

    fun updateProfileInList(updated: SleepProfile) {
        profiles = profiles.map { if (it.profileId == updated.profileId) updated else it }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SleepBackground)
            .systemBarsPadding()
    ) {
        TopNavBar(
            currentTab = currentTab,
            selectedSeat = selectedSeat,
            onTabSelected = {
                currentTab = it
                isSeatMenuOpen = false
            },
            onSeatButtonClick = { isSeatMenuOpen = !isSeatMenuOpen }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            // 관리 탭이 아닌데 프로필이 0개(빈 목록)인 경우 크래시 대신 첫 프로필 생성 안내 화면 표시!
            if (currentTab != SleepCareTab.MANAGE && currentProfile == null) {
                EmptyProfileGuideCard(
                    onGoToCreateClick = { currentTab = SleepCareTab.MANAGE }
                )
            } else {
                when (currentTab) {
                    SleepCareTab.HOME -> {
                        currentProfile?.let { profile ->
                            HomeScreen(
                                selectedSeat = selectedSeat,
                                runningSeats = runningSeats,
                                profiles = profiles,
                                currentProfile = profile,
                                onSeatSelected = { selectedSeat = it },
                                onProfileSelected = { newProfile ->
                                    seatProfileIdMap = seatProfileIdMap + (selectedSeat to newProfile.profileId)
                                },
                                onConfirmPresetClick = { currentTab = SleepCareTab.PRESET },
                                onGoToRunningClick = { currentTab = SleepCareTab.RUNNING },
                                onStartSleepClick = { runningSeats = runningSeats + selectedSeat },
                                onStopSleepClick = { runningSeats = runningSeats - selectedSeat }
                            )
                        }
                    }
                    SleepCareTab.PRESET -> {
                        currentProfile?.let { profile ->
                            PresetScreen(
                                selectedSeat = selectedSeat,
                                profiles = profiles,
                                currentProfile = profile,
                                onProfileSelected = { newProfile ->
                                    seatProfileIdMap = seatProfileIdMap + (selectedSeat to newProfile.profileId)
                                },
                                onCustomSettingClick = {
                                    selectedSettingSubTab = SettingSubTab.SEAT
                                    currentTab = SleepCareTab.SETTING
                                },
                                onStartSleepModeClick = {
                                    runningSeats = runningSeats + selectedSeat
                                    currentTab = SleepCareTab.RUNNING
                                },
                                onAddPresetClick = { currentTab = SleepCareTab.MANAGE }
                            )
                        }
                    }
                    SleepCareTab.RUNNING -> {
                        currentProfile?.let { profile ->
                            RunningScreen(
                                selectedSeat = selectedSeat,
                                currentProfile = profile,
                                onAdjustClick = { targetSubTab ->
                                    selectedSettingSubTab = targetSubTab
                                    currentTab = SleepCareTab.SETTING
                                },
                                onExitSleepMode = { _ ->
                                    runningSeats = runningSeats - selectedSeat
                                    currentTab = SleepCareTab.HOME
                                }
                            )
                        }
                    }
                    SleepCareTab.SETTING -> {
                        currentProfile?.let { profile ->
                            SettingScreen(
                                currentProfile = profile,
                                initialSubTab = selectedSettingSubTab,
                                onSaveProfilePreset = { updatedProfile ->
                                    updateProfileInList(updatedProfile)
                                },
                                onApplyAndRun = { updatedProfile ->
                                    updateProfileInList(updatedProfile)
                                    runningSeats = runningSeats + selectedSeat
                                    currentTab = SleepCareTab.RUNNING
                                }
                            )
                        }
                    }
                    SleepCareTab.MANAGE -> {
                        ManageScreen(
                            profiles = profiles,
                            onAddProfile = { newName ->
                                val nextId = (profiles.maxOfOrNull { it.profileId } ?: 0L) + 1L
                                val newProfile = SleepProfile(
                                    profileId = nextId,
                                    name = newName,
                                    backrestAngle = 128,
                                    seatHeatLevel = 1,
                                    seatVentLevel = 0,
                                    targetCelsius = 23.5,
                                    mood = MoodType.AMBER
                                )
                                profiles = profiles + newProfile
                                seatProfileIdMap = seatProfileIdMap + (selectedSeat to nextId)
                            },
                            onRenameProfile = { targetId, newName ->
                                profiles = profiles.map {
                                    if (it.profileId == targetId) it.copy(name = newName) else it
                                }
                            },
                            onDeleteProfile = { targetId ->
                                profiles = profiles.filterNot { it.profileId == targetId }
                                if (profiles.isEmpty()) {
                                    runningSeats = emptySet()
                                }
                            },
                            onEditPresetClick = { targetProfile ->
                                seatProfileIdMap = seatProfileIdMap + (selectedSeat to targetProfile.profileId)
                                selectedSettingSubTab = SettingSubTab.SEAT
                                currentTab = SleepCareTab.SETTING
                            }
                        )
                    }
                }
            }

            if (isSeatMenuOpen) {
                SeatSelectionPopover(
                    selectedSeat = selectedSeat,
                    onSeatSelected = { selectedSeat = it },
                    onGoToHomeFloorplan = { currentTab = SleepCareTab.HOME },
                    onDismiss = { isSeatMenuOpen = false }
                )
            }
        }
    }
}

@Composable
fun EmptyProfileGuideCard(
    onGoToCreateClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(520.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(SleepSurface)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "🌙", fontSize = 36.sp)
            Text(
                text = "아직 등록된 슬립 프로필이 없습니다",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "가족 구성원의 첫 슬립케어 프리셋을 추가하면 좌석별 맞춤 제어를 시작할 수 있습니다.",
                color = TextSecondary,
                fontSize = 14.sp
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AmberPrimary)
                    .clickable { onGoToCreateClick() }
                    .padding(horizontal = 28.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+ 첫 슬립 프로필 만들기",
                    color = TextOnAmber,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
fun SleepCareAppPreview() {
    NGMBTheme {
        SleepCareApp()
    }
}