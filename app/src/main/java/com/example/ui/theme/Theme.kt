package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

enum class ThemeMode(val label: String) {
    LIGHT("Light"),
    DARK_NEON("Dark (Neon)"),
    SYSTEM("System Default")
}

val FinSmartShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun FinSmartTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    textScale: Float = 1.0f,
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val useDarkNeon = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK_NEON -> true
        ThemeMode.SYSTEM -> systemDark
    }

    val finColors = when {
        useDarkNeon && highContrast -> HighContrastDarkColors
        useDarkNeon -> DarkNeonFinSmartColors
        highContrast -> HighContrastLightColors
        else -> LightFinSmartColors
    }

    val materialScheme = if (useDarkNeon) {
        darkColorScheme(
            primary = finColors.primary,
            onPrimary = Color(0xFF042113),
            primaryContainer = finColors.mintTint,
            onPrimaryContainer = finColors.primary,
            secondary = finColors.secondary,
            onSecondary = Color(0xFF031E26),
            secondaryContainer = finColors.cyanTint,
            onSecondaryContainer = finColors.secondary,
            tertiary = finColors.accentPurple,
            onTertiary = Color.White,
            tertiaryContainer = finColors.lavenderTint,
            onTertiaryContainer = finColors.textPrimary,
            background = finColors.background,
            onBackground = finColors.textPrimary,
            surface = finColors.surface,
            onSurface = finColors.textPrimary,
            surfaceVariant = finColors.elevatedSurface,
            onSurfaceVariant = finColors.textSecondary,
            error = finColors.danger,
            onError = Color.White,
            outline = finColors.cardBorder
        )
    } else {
        lightColorScheme(
            primary = finColors.primary,
            onPrimary = Color.White,
            primaryContainer = finColors.mintTint,
            onPrimaryContainer = Color(0xFF054733),
            secondary = finColors.secondary,
            onSecondary = Color.White,
            secondaryContainer = finColors.cyanTint,
            onSecondaryContainer = Color(0xFF004C73),
            tertiary = finColors.accentPurple,
            onTertiary = Color.White,
            tertiaryContainer = finColors.lavenderTint,
            onTertiaryContainer = Color(0xFF4C1D95),
            background = finColors.background,
            onBackground = finColors.textPrimary,
            surface = finColors.surface,
            onSurface = finColors.textPrimary,
            surfaceVariant = finColors.secondaryBackground,
            onSurfaceVariant = finColors.textSecondary,
            error = finColors.danger,
            onError = Color.White,
            outline = finColors.cardBorder
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !useDarkNeon
                    isAppearanceLightNavigationBars = !useDarkNeon
                }
            }
        }
    }

    CompositionLocalProvider(LocalFinSmartColors provides finColors) {
        MaterialTheme(
            colorScheme = materialScheme,
            typography = buildFinSmartTypography(textScale),
            shapes = FinSmartShapes,
            content = content
        )
    }
}
