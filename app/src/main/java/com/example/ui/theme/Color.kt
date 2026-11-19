package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class FinSmartColors(
    val isDarkNeon: Boolean,
    val background: Color,
    val secondaryBackground: Color,
    val surface: Color,
    val elevatedSurface: Color,
    val primary: Color,
    val secondary: Color,
    val accentPurple: Color,
    val accentBlue: Color,
    val accentPink: Color,
    val accentAmber: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val success: Color,
    val warning: Color,
    val danger: Color,
    val cardBorder: Color,
    val mintTint: Color,
    val cyanTint: Color,
    val lavenderTint: Color,
    val softBlueTint: Color,
    val amberTint: Color,
    val coralTint: Color,
    val navBarSurface: Color
)

val LightFinSmartColors = FinSmartColors(
    isDarkNeon = false,
    background = Color(0xFFF5F9FF),
    secondaryBackground = Color(0xFFECF3FC),
    surface = Color(0xFFFFFFFF),
    elevatedSurface = Color(0xFFFFFFFF),
    primary = Color(0xFF087F5B),
    secondary = Color(0xFF0088CC),
    accentPurple = Color(0xFF7C3AED),
    accentBlue = Color(0xFF2563EB),
    accentPink = Color(0xFFDB2777),
    accentAmber = Color(0xFFD97706),
    textPrimary = Color(0xFF142B32),
    textSecondary = Color(0xFF60737B),
    success = Color(0xFF16A36A),
    warning = Color(0xFFF59E0B),
    danger = Color(0xFFDC3545),
    cardBorder = Color(0xFFE0EBF5),
    mintTint = Color(0xFFC8F7E8),
    cyanTint = Color(0xFFD8F3FF),
    lavenderTint = Color(0xFFEEE5FF),
    softBlueTint = Color(0xFFDDEEFF),
    amberTint = Color(0xFFFFF2D4),
    coralTint = Color(0xFFFFE5E8),
    navBarSurface = Color(0xFFFFFFFF)
)

val DarkNeonFinSmartColors = FinSmartColors(
    isDarkNeon = true,
    background = Color(0xFF070B18),
    secondaryBackground = Color(0xFF0D1428),
    surface = Color(0xFF121D35),
    elevatedSurface = Color(0xFF192744),
    primary = Color(0xFF39FF88),
    secondary = Color(0xFF00E5FF),
    accentPurple = Color(0xFFA855F7),
    accentBlue = Color(0xFF3987FF),
    accentPink = Color(0xFFFF4FD8),
    accentAmber = Color(0xFFFFB020),
    textPrimary = Color(0xFFF4F7FF),
    textSecondary = Color(0xFFA8B4CC),
    success = Color(0xFF39FF88),
    warning = Color(0xFFFFB020),
    danger = Color(0xFFFF4567),
    cardBorder = Color(0xFF273963),
    mintTint = Color(0xFF0D2E2A),
    cyanTint = Color(0xFF0B273D),
    lavenderTint = Color(0xFF23173E),
    softBlueTint = Color(0xFF102242),
    amberTint = Color(0xFF2B2112),
    coralTint = Color(0xFF341423),
    navBarSurface = Color(0xFF0B1122)
)

val HighContrastLightColors = LightFinSmartColors.copy(
    background = Color(0xFFFFFFFF),
    textPrimary = Color(0xFF000000),
    textSecondary = Color(0xFF1F2937),
    cardBorder = Color(0xFF475569)
)

val HighContrastDarkColors = DarkNeonFinSmartColors.copy(
    background = Color(0xFF000000),
    surface = Color(0xFF0D1528),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFDDE6F8),
    cardBorder = Color(0xFF00E5FF)
)

val LocalFinSmartColors = staticCompositionLocalOf { LightFinSmartColors }
