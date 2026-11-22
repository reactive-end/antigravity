package com.opensource.antigravity.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object AntigravityColors {
    val Canvas = Color(0xFF000000)
    val Surface = Color(0xFF121318)
    val Card = Color(0xFF161821)
    val Border = Color(0xFF232632)
    val InkPrimary = Color(0xFFECEEF4)
    val InkSecondary = Color(0xFF8A8F9E)
    val AccentBlue = Color(0xFF4C8BF5)
    val AccentCyan = Color(0xFF38BDF8)
    val AccentViolet = Color(0xFF8B5CF6)
    val Danger = Color(0xFFEF4444)

    val ProgressBrush: Brush = Brush.horizontalGradient(
        colors = listOf(AccentBlue, AccentViolet, AccentCyan),
    )
}

private val AntigravityDarkScheme = darkColorScheme(
    primary = AntigravityColors.AccentBlue,
    secondary = AntigravityColors.AccentCyan,
    background = AntigravityColors.Canvas,
    surface = AntigravityColors.Surface,
    onPrimary = Color.White,
    onBackground = AntigravityColors.InkPrimary,
    onSurface = AntigravityColors.InkPrimary,
    outline = AntigravityColors.Border,
    error = AntigravityColors.Danger,
)

private val AntigravityTypography = Typography(
    headlineSmall = TextStyle(
        fontSize = 21.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = (-0.3).sp,
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
    ),
)

@Composable
fun AntigravityTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AntigravityDarkScheme,
        typography = AntigravityTypography,
        content = content,
    )
}
