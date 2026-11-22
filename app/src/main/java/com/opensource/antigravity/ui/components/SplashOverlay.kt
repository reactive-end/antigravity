package com.opensource.antigravity.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.opensource.antigravity.ui.theme.LocalAntigravityPalette

@Composable
fun SplashOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAntigravityPalette.current
    AnimatedVisibility(
        visible = visible,
        exit = fadeOut(animationSpec = tween(380, easing = FastOutSlowInEasing)) +
            scaleOut(targetScale = 1.06f, animationSpec = tween(380, easing = FastOutSlowInEasing)),
        modifier = modifier,
    ) {
        val transition = rememberInfiniteTransition(label = "antigravity_splash")
        val orbitAngle by transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3600, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "orbit_angle",
        )
        val glowScale by transition.animateFloat(
            initialValue = 0.88f,
            targetValue = 1.12f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "glow_scale",
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(palette.canvas),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                AntigravityOrbitalEmblem(
                    orbitAngle = orbitAngle,
                    glowScale = glowScale,
                    accentStart = palette.accentStart,
                    accentMid = palette.accentMid,
                    accentEnd = palette.accentEnd,
                )
                Spacer(modifier = Modifier.height(22.dp))
                Text(
                    text = "Antigravity",
                    style = MaterialTheme.typography.headlineSmall,
                    color = palette.inkPrimary,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Conectando espacio remoto...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.inkSecondary,
                )
            }
        }
    }
}

@Composable
private fun AntigravityOrbitalEmblem(
    orbitAngle: Float,
    glowScale: Float,
    accentStart: Color,
    accentMid: Color,
    accentEnd: Color,
) {
    Canvas(modifier = Modifier.size(116.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val glowRadius = (size.minDimension * 0.48f) * glowScale

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(accentStart.copy(alpha = 0.30f), Color.Transparent),
                center = center,
                radius = glowRadius,
            ),
            radius = glowRadius,
            center = center,
        )

        rotate(degrees = orbitAngle, pivot = center) {
            drawOval(
                brush = Brush.sweepGradient(
                    colors = listOf(accentStart, accentMid, accentEnd, accentStart),
                    center = center,
                ),
                topLeft = Offset(size.width * 0.10f, size.height * 0.30f),
                size = Size(size.width * 0.80f, size.height * 0.40f),
                style = Stroke(width = 5f, cap = StrokeCap.Round),
            )
        }

        val rOuter = size.minDimension * 0.28f
        val rInner = size.minDimension * 0.09f
        val star = Path().apply {
            moveTo(center.x, center.y - rOuter)
            lineTo(center.x + rInner, center.y - rInner)
            lineTo(center.x + rOuter, center.y)
            lineTo(center.x + rInner, center.y + rInner)
            lineTo(center.x, center.y + rOuter)
            lineTo(center.x - rInner, center.y + rInner)
            lineTo(center.x - rOuter, center.y)
            lineTo(center.x - rInner, center.y - rInner)
            close()
        }
        drawPath(
            path = star,
            brush = Brush.linearGradient(
                colors = listOf(accentStart, accentEnd),
                start = Offset(center.x - rOuter, center.y - rOuter),
                end = Offset(center.x + rOuter, center.y + rOuter),
            ),
        )
    }
}
