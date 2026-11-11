package com.hyundai.ngmb

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
fun ManageScreen(
    profiles: List<SleepProfile>,
    onAddProfile: (name: String) -> Unit,
    onRenameProfile: (profileId: Long, newName: String) -> Unit,
    onDeleteProfile: (profileId: Long) -> Unit,
    onEditPresetClick: (SleepProfile) -> Unit
) {
    // 피그마 Screen 05 상단 '운전석 슬립 불가 및 등받이 안전 안내' 배너 표시 여부
    var showSafetyBanner by remember { mutableStateOf(true) }

    // 모달 상태 관리 (추가 모달 / 이름 변경 대상 프로필 / 삭제 확인 대상 프로필)
    var showAddModal by remember { mutableStateOf(false) }
    var renameTarget by remember { mutableStateOf<SleepProfile?>(null) }
    var deleteTarget by remember { mutableStateOf<SleepProfile?>(null) }
    var limitErrorBanner by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. 상단 타이틀 + [+ 새 프로필 추가] 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "PROFILE & PRESET MANAGEMENT (${profiles.size}/10)",
                        color = AmberPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "슬립케어 프리셋 관리",
                        color = TextPrimary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 우측 [+ 새 프로필 추가] 버튼 (명세서: 최대 10개 제한)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(AmberPrimary)
                        .clickable {
                            if (profiles.size >= 10) {
                                limitErrorBanner = true
                            } else {
                                limitErrorBanner = false
                                showAddModal = true
                            }
                        }
                        .padding(horizontal = 22.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+ 새 프로필 추가",
                        color = TextOnAmber,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 409 에러 대응: 프로필 10개 초과 안내 배너
            if (limitErrorBanner) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF3B1D1D))
                        .border(1.dp, DangerRed, RoundedCornerShape(12.dp))
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚠️ 프로필은 최대 10개입니다. 기존 프로필을 삭제한 뒤 추가해 주세요.",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "닫기",
                        color = AmberPrimary,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable { limitErrorBanner = false }
                    )
                }
            }

            // 2. 피그마 Screen 05의 '운전석 슬립 불가 및 등받이 안전 안내' 배너
            if (showSafetyBanner) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SleepCardSelected)
                        .border(1.dp, SleepBorder, RoundedCornerShape(16.dp))
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SleepSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🛡️", fontSize = 18.sp)
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(DangerRed)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "주행 안전 잠금",
                                        color = TextPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "운전석 슬립 불가 및 등받이 안전 안내",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "운전석은 주행 안전을 위해 슬립 모드를 지원하지 않습니다. 또한 주행 중에는 탑승자 안전벨트 유효 구속을 위해 등받이 각도 조절이 제한됩니다. (정차 시 전각도 조절 가능)",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(SleepCardSurface)
                            .border(1.dp, SleepBorder, RoundedCornerShape(16.dp))
                            .clickable { showSafetyBanner = false }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(text = "확인", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 3. 등록된 프로필 목록 (스크롤 가능)
            if (profiles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SleepSurface)
                        .border(1.dp, SleepBorder, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "등록된 프로필이 없습니다. 우측 상단 [+ 새 프로필 추가] 버튼을 눌러 첫 프로필을 생성해 주세요.",
                        color = TextSecondary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    profiles.forEach { profile ->
                        ManageProfileRow(
                            profile = profile,
                            canDelete = true,
                            onRenameClick = { renameTarget = profile },
                            onEditPresetClick = { onEditPresetClick(profile) },
                            onDeleteClick = { deleteTarget = profile }
                        )
                    }
                }
            }
        }

        // 모달 1: 새 프로필 추가 (POST /api/v1/profiles 대응)
        if (showAddModal) {
            ProfileNameInputModal(
                title = "새 프로필 추가",
                subtitle = "초기 프리셋과 함께 새 가족 프로필을 생성합니다 (1~20자)",
                initialName = "",
                confirmText = "생성하기",
                onConfirm = { newName ->
                    onAddProfile(newName)
                    showAddModal = false
                },
                onDismiss = { showAddModal = false }
            )
        }

        // 모달 2: 프로필 이름 변경 (PATCH /api/v1/profiles/{profileId} 대응)
        renameTarget?.let { target ->
            ProfileNameInputModal(
                title = "프로필 이름 변경",
                subtitle = "'${target.name}' 프로필의 새 이름을 입력해 주세요 (1~20자)",
                initialName = target.name,
                confirmText = "변경 저장",
                onConfirm = { newName ->
                    onRenameProfile(target.profileId, newName)
                    renameTarget = null
                },
                onDismiss = { renameTarget = null }
            )
        }

        // 모달 3: 프로필 삭제 확인 창 (DELETE /api/v1/profiles/{profileId} 대응)
        deleteTarget?.let { target ->
            DeleteConfirmModal(
                profile = target,
                onConfirmDelete = {
                    onDeleteProfile(target.profileId)
                    deleteTarget = null
                },
                onDismiss = { deleteTarget = null }
            )
        }
    }
}

@Composable
fun ManageProfileRow(
    profile: SleepProfile,
    canDelete: Boolean,
    onRenameClick: () -> Unit,
    onEditPresetClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SleepSurface)
            .border(1.dp, SleepBorder, RoundedCornerShape(18.dp))
            .padding(horizontal = 22.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 좌측: 아바타 + 이름 + 현재 저장된 프리셋 요약 뱃지들
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SleepCardSurface)
                    .border(1.dp, SleepBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = profile.avatarEmoji, fontSize = 22.sp)
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = profile.name,
                        color = TextPrimary,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = profile.profileTag,
                        color = AmberPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallPresetTag(text = "🛋️ ${profile.backrestSummary}")
                    SmallPresetTag(text = "♨️ ${profile.seatHeatSummary}")
                    SmallPresetTag(text = "🌡️ ${profile.tempSummary}")
                    SmallPresetTag(text = "☀️ ${profile.moodSummary}")
                }
            }
        }

        // 우측: [이름 변경] / [프리셋 수정] / [삭제] 버튼
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SleepCardSurface)
                    .border(1.dp, SleepBorder, RoundedCornerShape(12.dp))
                    .clickable { onRenameClick() }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(text = "✏️ 이름 변경", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(SleepCardSelected)
                    .border(1.dp, AmberPrimary, RoundedCornerShape(12.dp))
                    .clickable { onEditPresetClick() }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(text = "🎚️ 프리셋 수정", color = AmberPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            if (canDelete) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF3B1D1D))
                        .border(1.dp, DangerRed, RoundedCornerShape(12.dp))
                        .clickable { onDeleteClick() }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(text = "🗑️ 삭제", color = Color(0xFFE57373), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SmallPresetTag(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SleepCardSurface)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = TextSecondary, fontSize = 12.sp)
    }
}

@Composable
fun ProfileNameInputModal(
    title: String,
    subtitle: String,
    initialName: String,
    confirmText: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var inputText by remember { mutableStateOf(initialName) }
    val trimmed = inputText.trim()
    val isValid = trimmed.length in 1..20

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(460.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(SleepSurface)
                .border(1.dp, SleepBorder, RoundedCornerShape(22.dp))
                .clickable(enabled = false) {}
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = title, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = TextSecondary, fontSize = 13.sp)

            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                singleLine = true,
                placeholder = { Text("예: 엄마, 막내동생 (1~20자)", color = TextMuted) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = AmberPrimary,
                    unfocusedBorderColor = SleepBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (!isValid && inputText.isNotEmpty()) {
                Text(
                    text = "이름은 앞뒤 공백 제외 1~20자여야 합니다.",
                    color = Color(0xFFE57373),
                    fontSize = 12.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleepCardSurface)
                        .clickable { onDismiss() }
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Text(text = "취소", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isValid) AmberPrimary else SleepCardSurface)
                        .clickable(enabled = isValid) { onConfirm(trimmed) }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = confirmText,
                        color = if (isValid) TextOnAmber else TextMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmModal(
    profile: SleepProfile,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(460.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(SleepSurface)
                .border(1.dp, SleepBorder, RoundedCornerShape(22.dp))
                .clickable(enabled = false) {}
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "'${profile.name}' 프로필을 삭제할까요?",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "저장된 슬립 모드 프리셋도 함께 삭제되며 되돌릴 수 없습니다.",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleepCardSurface)
                        .clickable { onDismiss() }
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Text(text = "취소", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(DangerRed)
                        .clickable { onConfirmDelete() }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(text = "삭제하기", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}