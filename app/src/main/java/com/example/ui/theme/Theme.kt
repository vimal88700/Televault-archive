package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppThemeMode(val displayName: String) {
    DARK("Midnight Dark"),
    AMOLED("AMOLED Pitch Black"),
    LIGHT("Clean Light"),
    SYSTEM("Follow System")
}

private val DarkColorScheme = darkColorScheme(
    primary = TeleBlue,
    onPrimary = TextPrimary,
    primaryContainer = TeleBlueDark,
    onPrimaryContainer = TextPrimary,
    secondary = ShieldGreen,
    onSecondary = BackgroundDark,
    secondaryContainer = SurfaceElevatedDark,
    onSecondaryContainer = ShieldGreen,
    tertiary = TeleCyan,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevatedDark,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceHighlightDark
)

private val AmoledColorScheme = darkColorScheme(
    primary = TeleBlue,
    onPrimary = TextPrimary,
    primaryContainer = TeleBlueDark,
    onPrimaryContainer = TextPrimary,
    secondary = ShieldGreen,
    onSecondary = BackgroundAmoled,
    secondaryContainer = SurfaceElevatedAmoled,
    onSecondaryContainer = ShieldGreen,
    tertiary = TeleCyan,
    background = BackgroundAmoled,
    onBackground = TextPrimary,
    surface = SurfaceAmoled,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevatedAmoled,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceHighlightAmoled
)

private val LightColorScheme = lightColorScheme(
    primary = TeleBlueDeep,
    onPrimary = TextPrimary,
    primaryContainer = TeleBlue,
    onPrimaryContainer = TextPrimary,
    secondary = ShieldGreenDark,
    onSecondary = TextPrimary,
    secondaryContainer = SurfaceElevatedLight,
    onSecondaryContainer = TextPrimaryLight,
    tertiary = TeleCyan,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceElevatedLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFFD0D8E0)
)

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val systemInDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.DARK, AppThemeMode.AMOLED -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SYSTEM -> systemInDark
    }

    val colorScheme = when (themeMode) {
        AppThemeMode.DARK -> DarkColorScheme
        AppThemeMode.AMOLED -> AmoledColorScheme
        AppThemeMode.LIGHT -> LightColorScheme
        AppThemeMode.SYSTEM -> if (systemInDark) DarkColorScheme else LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
