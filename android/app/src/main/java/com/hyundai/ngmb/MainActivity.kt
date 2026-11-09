package com.hyundai.ngmb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    // 전체 프로필 목록 상태 (추가/이름변경/삭제 가능!)
    var profiles by remember { mutableStateOf(defaultProfiles) }

    // 각 좌석별로 현재 선택된 프로필 ID를 기억
    var seatProfileIdMap by remember {
        mutableStateOf(
            mapOf(
                SeatZone.PASSENGER to 1,
                SeatZone.REAR_LEFT to 2,
                SeatZone.REAR_RIGHT to 3
            )
        )
    }

    // 현재 선택된 좌석의 프로필 꺼내오기
    val currentProfileId = seatProfileIdMap[selectedSeat] ?: profiles.first().profileId
    val currentProfile = profiles.find { it.profileId == currentProfileId } ?: profiles.first()

    // 프로필 프리셋이 수정되었을 때 목록을 업데이트하는 헬퍼 함수
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
            when (currentTab) {
                SleepCareTab.HOME -> {
                    HomeScreen(
                        selectedSeat = selectedSeat,
                        runningSeats = runningSeats,
                        profiles = profiles,
                        currentProfile = currentProfile,
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
                SleepCareTab.PRESET -> {
                    PresetScreen(
                        selectedSeat = selectedSeat,
                        profiles = profiles,
                        currentProfile = currentProfile,
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
                SleepCareTab.RUNNING -> {
                    RunningScreen(
                        selectedSeat = selectedSeat,
                        currentProfile = currentProfile,
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
                SleepCareTab.SETTING -> {
                    SettingScreen(
                        currentProfile = currentProfile,
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
                SleepCareTab.MANAGE -> {
                    ManageScreen(
                        profiles = profiles,
                        onAddProfile = { newName ->
                            val nextId = (profiles.maxOfOrNull { it.profileId } ?: 0) + 1
                            val newProfile = SleepProfile(
                                profileId = nextId,
                                name = newName,
                                avatarEmoji = "🙂",
                                profileTag = "맞춤 슬립 프로필",
                                backrestSummary = "등받이 릴랙스",
                                seatHeatSummary = "열선 1단계",
                                tempSummary = "23.5°C",
                                moodSummary = "앰버 무드"
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
                            if (profiles.size > 1) {
                                profiles = profiles.filterNot { it.profileId == targetId }
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

@Preview(showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
fun SleepCareAppPreview() {
    NGMBTheme {
        SleepCareApp()
    }
}