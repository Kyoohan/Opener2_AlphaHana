package com.kyoohan.opener2.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = BackgroundColor,
    surface = SurfaceColor,
    onBackground = TextColor,
    onSurface = TextColor
)

@Composable
fun Opener2Theme(
    darkTheme: Boolean = true, // Always use dark theme for this app
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disable dynamic color to use our custom theme
    highContrastMode: Boolean = false,
    accentColorPreset: Int = 0,
    content: @Composable () -> Unit
) {
    // Accent Color에 따라 primary/secondary 색상 선택
    val (primaryColor, secondaryColor) = when (accentColorPreset) {
        1 -> Pair(AccentCoolStart, AccentCoolEnd) // 시원
        2 -> Pair(AccentPurpleStart, AccentPurpleEnd) // 보라
        3 -> Pair(AccentGreenStart, AccentGreenEnd) // 초록
        else -> Pair(AccentWarmStart, AccentWarmEnd) // 따뜻 (기본)
    }
    
    val colorScheme = if (highContrastMode) {
        // 고대비 모드: accent color 무시하고 노란색 고정 (저시력자용)
        darkColorScheme(
            primary = HighContrastTextColor,
            secondary = HighContrastTextColor,
            tertiary = GradientStartDefault,
            background = HighContrastBackgroundColor,
            surface = HighContrastSurfaceColor,
            onBackground = HighContrastTextColor,
            onSurface = HighContrastTextColor,
            onPrimary = Color.Black,
            onSecondary = Color.Black,
            onTertiary = HighContrastTextColor
        )
    } else {
        // 일반 모드: accent color 적용
        darkColorScheme(
            primary = primaryColor,
            secondary = secondaryColor,
            tertiary = GradientStartDefault,
            background = BackgroundColor,
            surface = SurfaceColor,
            onBackground = TextColor,
            onSurface = TextColor,
            onPrimary = Color.Black,
            onSecondary = Color.Black,
            onTertiary = TextColor
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}