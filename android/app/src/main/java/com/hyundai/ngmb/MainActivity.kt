package com.hyundai.ngmb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hyundai.ngmb.ui.theme.*
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val coroutineScope = rememberCoroutineScope()

    var currentTab by remember { mutableStateOf(SleepCareTab.HOME) }
    var selectedSeat by remember { mutableStateOf(SeatZone.PASSENGER) }
    var isSeatMenuOpen by remember { mutableStateOf(false) }
    var selectedSettingSubTab by remember { mutableStateOf(SettingSubTab.SEAT) }
    var runningSeats by remember { mutableStateOf(setOf<SeatZone>()) }

    // Preview에서는 기본 프로필 3개를 보여주고, 실제 앱에서는 서버 응답을 받아 채움
    var profiles by remember { mutableStateOf(if (isPreview) defaultProfiles else emptyList()) }
    var seatProfileIdMap by remember { mutableStateOf(emptyMap<SeatZone, Long>()) }
    var apiErrorMessage by remember { mutableStateOf<String?>(null) }

    // 서버에서 프로필 목록 다시 불러오기 (404 발생 시 및 최초 진입 시 호출)
    fun reloadProfilesFromServer() {
        if (isPreview) return
        coroutineScope.launch {
            runCatching {
                SleepCareApiClient.listProfiles(context)
            }.onSuccess { serverProfiles ->
                profiles = serverProfiles
                apiErrorMessage = null
            }.onFailure { error ->
                // 500 또는 서버 미실행 시 기기에 저장된 기본 프로필로 계속 진행
                if (profiles.isEmpty()) {
                    profiles = defaultProfiles
                }
                if (error is ApiCallException && error.httpStatus in 400..499) {
                    apiErrorMessage = error.message
                }
            }
        }
    }

    // 앱 최초 진입 시 서버에서 내 프로필 목록 조회 (GET /api/v1/profiles)
    LaunchedEffect(Unit) {
        reloadProfilesFromServer()
    }

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

    // 서버 API 에러 공통 처리 (404면 목록 재조회, 400/409면 화면에 에러 사유 표시)
    fun handleApiFailure(error: Throwable, fallbackAction: () -> Unit = {}) {
        if (error is ApiCallException) {
            when (error.httpStatus) {
                404 -> {
                    apiErrorMessage = "프로필을 찾을 수 없어 목록을 다시 불러옵니다."
                    reloadProfilesFromServer()
                }
                400, 409 -> {
                    apiErrorMessage = error.message
                }
                else -> {
                    fallbackAction()
                }
            }
        } else {
            // 로컬 서버가 꺼져 있을 때는 로컬 상태로 폴백 동작
            fallbackAction()
        }
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

        // 서버 400(fieldErrors) / 409(개수 초과) / 404 안내 배너
        apiErrorMessage?.let { message ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF3B1D1D))
                    .border(1.dp, DangerRed, RoundedCornerShape(12.dp))
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚠️ $message",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "닫기",
                    color = AmberPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { apiErrorMessage = null }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
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
                                    // PUT /api/v1/profiles/{profileId}/preset 호출
                                    coroutineScope.launch {
                                        runCatching {
                                            SleepCareApiClient.updatePreset(
                                                context = context,
                                                profileId = updatedProfile.profileId,
                                                updated = updatedProfile
                                            )
                                        }.onSuccess { savedFromServer ->
                                            updateProfileInList(savedFromServer)
                                            apiErrorMessage = null
                                        }.onFailure { error ->
                                            handleApiFailure(error) {
                                                updateProfileInList(updatedProfile)
                                            }
                                        }
                                    }
                                },
                                onApplyAndRun = { updatedProfile ->
                                    // PUT /api/v1/profiles/{profileId}/preset 호출 후 실행 화면으로 전환
                                    coroutineScope.launch {
                                        runCatching {
                                            SleepCareApiClient.updatePreset(
                                                context = context,
                                                profileId = updatedProfile.profileId,
                                                updated = updatedProfile
                                            )
                                        }.onSuccess { savedFromServer ->
                                            updateProfileInList(savedFromServer)
                                            apiErrorMessage = null
                                            runningSeats = runningSeats + selectedSeat
                                            currentTab = SleepCareTab.RUNNING
                                        }.onFailure { error ->
                                            handleApiFailure(error) {
                                                updateProfileInList(updatedProfile)
                                                runningSeats = runningSeats + selectedSeat
                                                currentTab = SleepCareTab.RUNNING
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                    SleepCareTab.MANAGE -> {
                        ManageScreen(
                            profiles = profiles,
                            onAddProfile = { newName ->
                                val template = SleepProfile(
                                    profileId = 0L,
                                    name = newName,
                                    backrestAngle = 128,
                                    seatHeatLevel = 1,
                                    seatVentLevel = 0,
                                    targetCelsius = 23.5,
                                    mood = MoodType.AMBER
                                )
                                // POST /api/v1/profiles 호출 (서버가 생성한 profileId 사용!)
                                coroutineScope.launch {
                                    runCatching {
                                        SleepCareApiClient.createProfile(context, newName, template)
                                    }.onSuccess { createdFromServer ->
                                        profiles = profiles + createdFromServer
                                        seatProfileIdMap = seatProfileIdMap + (selectedSeat to createdFromServer.profileId)
                                        apiErrorMessage = null
                                    }.onFailure { error ->
                                        handleApiFailure(error) {
                                            val fallbackId = (profiles.maxOfOrNull { it.profileId } ?: 0L) + 1L
                                            val localCreated = template.copy(profileId = fallbackId)
                                            profiles = profiles + localCreated
                                            seatProfileIdMap = seatProfileIdMap + (selectedSeat to fallbackId)
                                        }
                                    }
                                }
                            },
                            onRenameProfile = { targetId, newName ->
                                // PATCH /api/v1/profiles/{profileId} 호출
                                coroutineScope.launch {
                                    runCatching {
                                        SleepCareApiClient.renameProfile(context, targetId, newName)
                                    }.onSuccess { renamedFromServer ->
                                        updateProfileInList(renamedFromServer)
                                        apiErrorMessage = null
                                    }.onFailure { error ->
                                        handleApiFailure(error) {
                                            profiles = profiles.map {
                                                if (it.profileId == targetId) it.copy(name = newName) else it
                                            }
                                        }
                                    }
                                }
                            },
                            onDeleteProfile = { targetId ->
                                // DELETE /api/v1/profiles/{profileId} 호출
                                coroutineScope.launch {
                                    runCatching {
                                        SleepCareApiClient.deleteProfile(context, targetId)
                                    }.onSuccess {
                                        profiles = profiles.filterNot { it.profileId == targetId }
                                        if (profiles.isEmpty()) runningSeats = emptySet()
                                        apiErrorMessage = null
                                    }.onFailure { error ->
                                        handleApiFailure(error) {
                                            profiles = profiles.filterNot { it.profileId == targetId }
                                            if (profiles.isEmpty()) runningSeats = emptySet()
                                        }
                                    }
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