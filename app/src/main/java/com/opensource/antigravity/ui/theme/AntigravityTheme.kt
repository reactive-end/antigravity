package com.opensource.antigravity.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.opensource.antigravity.data.ThemeMode

@Immutable
data class AntigravityPalette(
    val canvas: Color,
    val surfaceElevated: Color,
    val surfaceGlass: Color,
    val borderHairline: Color,
    val inkPrimary: Color,
    val inkSecondary: Color,
    val accentStart: Color,
    val accentMid: Color,
    val accentEnd: Color,
    val onlineDot: Color,
    val danger: Color,
    val isDark: Boolean,
) {
    val cosmicBrush: Brush
        get() = Brush.horizontalGradient(listOf(accentStart, accentMid, accentEnd))
}

val DarkAntigravityPalette = AntigravityPalette(
    canvas = Color(0xFF000000),
    surfaceElevated = Color(0xFF111520),
    surfaceGlass = Color(0xE6151A26),
    borderHairline = Color(0xFF252D40),
    inkPrimary = Color(0xFFEEF2FA),
    inkSecondary = Color(0xFF8D98B0),
    accentStart = Color(0xFF5B8CFF),
    accentMid = Color(0xFF8B5CF6),
    accentEnd = Color(0xFF38BDF8),
    onlineDot = Color(0xFF22C55E),
    danger = Color(0xFFEF4444),
    isDark = true,
)

val LightAntigravityPalette = AntigravityPalette(
    canvas = Color(0xFFF6F8FC),
    surfaceElevated = Color(0xFFFFFFFF),
    surfaceGlass = Color(0xF0FFFFFF),
    borderHairline = Color(0xFFDCE3F0),
    inkPrimary = Color(0xFF0F172A),
    inkSecondary = Color(0xFF5B677D),
    accentStart = Color(0xFF3B6EF6),
    accentMid = Color(0xFF7C3AED),
    accentEnd = Color(0xFF0284C7),
    onlineDot = Color(0xFF16A34A),
    danger = Color(0xFFDC2626),
    isDark = false,
)

val LocalAntigravityPalette = staticCompositionLocalOf { DarkAntigravityPalette }

private val AntigravityTypography = Typography(
    headlineSmall = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.4).sp,
    ),
    titleMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.2).sp,
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.2.sp,
    ),
)

@Composable
fun AntigravityTheme(
    themeMode: ThemeMode,
    content: @Composable () -> Unit,
) {
    val useDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val palette = if (useDark) DarkAntigravityPalette else LightAntigravityPalette
    val colorScheme = if (useDark) {
        darkColorScheme(
            primary = palette.accentStart,
            secondary = palette.accentEnd,
            background = palette.canvas,
            surface = palette.surfaceElevated,
            onBackground = palette.inkPrimary,
            onSurface = palette.inkPrimary,
            outline = palette.borderHairline,
            error = palette.danger,
        )
    } else {
        lightColorScheme(
            primary = palette.accentStart,
            secondary = palette.accentEnd,
            background = palette.canvas,
            surface = palette.surfaceElevated,
            onBackground = palette.inkPrimary,
            onSurface = palette.inkPrimary,
            outline = palette.borderHairline,
            error = palette.danger,
        )
    }

    CompositionLocalProvider(LocalAntigravityPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AntigravityTypography,
            content = content,
        )
    }
}
