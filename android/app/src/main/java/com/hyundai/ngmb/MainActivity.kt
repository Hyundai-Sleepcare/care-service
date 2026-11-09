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
    // 현재 슬립 모드가 작동 중인 좌석들을 기억하는 집합(Set)
    var runningSeats by remember { mutableStateOf(setOf<SeatZone>()) }

    // 각 좌석별로 현재 선택된 프로필을 기억하는 상태 (조수석=아내, 2열좌측=첫째아들, 2열우측=둘째딸)
    var seatProfileMap by remember {
        mutableStateOf(
            mapOf(
                SeatZone.PASSENGER to defaultProfiles[0],
                SeatZone.REAR_LEFT to defaultProfiles[1],
                SeatZone.REAR_RIGHT to defaultProfiles[2]
            )
        )
    }

    // 현재 선택된 좌석의 프로필 꺼내오기
    val currentProfile = seatProfileMap[selectedSeat] ?: defaultProfiles[0]

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
                        profiles = defaultProfiles,
                        currentProfile = currentProfile,
                        onSeatSelected = { selectedSeat = it },
                        onProfileSelected = { newProfile ->
                            // 현재 좌석의 프로필을 새로 누른 프로필로 변경!
                            seatProfileMap = seatProfileMap + (selectedSeat to newProfile)
                        },
                        onConfirmPresetClick = { currentTab = SleepCareTab.PRESET },
                        onGoToRunningClick = { currentTab = SleepCareTab.RUNNING},
                        onStartSleepClick = {runningSeats = runningSeats + selectedSeat },
                        onStopSleepClick = { runningSeats = runningSeats - selectedSeat }
                    )
                }
                SleepCareTab.PRESET -> {
                    PresetScreen(
                        selectedSeat = selectedSeat,
                        profiles = defaultProfiles,
                        currentProfile = currentProfile,
                        onProfileSelected = { newProfile ->
                            seatProfileMap = seatProfileMap + (selectedSeat to newProfile)
                        },
                        onCustomSettingClick = { currentTab = SleepCareTab.SETTING },
                        onStartSleepModeClick = {
                            runningSeats = runningSeats + selectedSeat
                            currentTab = SleepCareTab.RUNNING
                        },
                        onAddPresetClick = { currentTab = SleepCareTab.SETTING }
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
                            // 종료 옵션 선택 시 홈 화면으로 복귀
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
                            seatProfileMap = seatProfileMap + (selectedSeat to updatedProfile)
                        },
                        onApplyAndRun = { updatedProfile ->
                            seatProfileMap = seatProfileMap + (selectedSeat to updatedProfile)
                            runningSeats = runningSeats + selectedSeat
                            currentTab = SleepCareTab.RUNNING
                        }
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "현재 탭: ${currentTab.title} / 선택 좌석: ${selectedSeat.label} (${currentProfile.name})",
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
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

@Preview(showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
fun SleepCareAppPreview() {
    NGMBTheme {
        SleepCareApp()
    }
}