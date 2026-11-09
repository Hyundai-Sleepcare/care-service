package com.hyundai.ngmb.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


private val SleepCareColorScheme = darkColorScheme(
    primary = AmberPrimary,
    onPrimary = TextOnAmber,
    background = SleepBackground,
    onBackground = TextPrimary,
    surface = SleepSurface,
    onSurface = TextPrimary,
    surfaceVariant = SleepCardSurface,
    onSurfaceVariant = TextSecondary,
    error = DangerRed
)
@Composable
fun NGMBTheme(
    content: @Composable () -> Unit
    ) {
    MaterialTheme (
        colorScheme = SleepCareColorScheme,
        typography = Typography,
        content = content
    )
}