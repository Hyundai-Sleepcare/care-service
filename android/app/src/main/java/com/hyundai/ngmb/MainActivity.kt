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
                        profiles = defaultProfiles,
                        currentProfile = currentProfile,
                        onSeatSelected = { selectedSeat = it },
                        onProfileSelected = { newProfile ->
                            // 현재 좌석의 프로필을 새로 누른 프로필로 변경!
                            seatProfileMap = seatProfileMap + (selectedSeat to newProfile)
                        },
                        onConfirmPresetClick = { currentTab = SleepCareTab.PRESET }
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