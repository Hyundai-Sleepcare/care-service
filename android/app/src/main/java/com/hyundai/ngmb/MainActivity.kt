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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SleepBackground)
            .systemBarsPadding()
    ) {
        // 1. 공통 상단 네비게이션 바
        TopNavBar(
            currentTab = currentTab,
            selectedSeat = selectedSeat,
            onTabSelected = {
                currentTab = it
                isSeatMenuOpen = false
                },
            onSeatButtonClick = { isSeatMenuOpen = !isSeatMenuOpen }
        )

        // 2. 탭에 따라 바뀌는 본문 영역
        Box(modifier = Modifier.fillMaxSize()) {
            // 본문 화면
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "현재 탭: ${currentTab.title} / 선택 좌석: ${selectedSeat.label}",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            // 좌석 버튼 눌렀을 때 우측 상단에 뜨는 팝오버
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