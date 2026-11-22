package com.opensource.antigravity.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.opensource.antigravity.ui.theme.AntigravityColors

@Composable
fun CosmicProgressBar(
    progress: Int,
    modifier: Modifier = Modifier,
) {
    val visible = progress in 1..99
    val fraction by animateFloatAsState(
        targetValue = (progress / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing),
        label = "progress_fraction",
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(120)),
        exit = fadeOut(tween(240)),
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color.Transparent),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(2.dp)
                    .background(AntigravityColors.ProgressBrush),
            )
        }
    }
}

@Composable
fun SplashOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        exit = fadeOut(animationSpec = tween(320, easing = FastOutSlowInEasing)) +
            scaleOut(targetScale = 1.04f, animationSpec = tween(320, easing = FastOutSlowInEasing)),
        modifier = modifier,
    ) {
        val transition = rememberInfiniteTransition(label = "antigravity_splash")
        val glowAlpha by transition.animateFloat(
            initialValue = 0.16f,
            targetValue = 0.34f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "glow_alpha",
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AntigravityColors.Canvas),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                AntigravityMark(glowAlpha = glowAlpha)
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Antigravity",
                    style = MaterialTheme.typography.headlineSmall,
                    color = AntigravityColors.InkPrimary,
                )
            }
        }
    }
}

@Composable
private fun AntigravityMark(glowAlpha: Float) {
    Canvas(modifier = Modifier.size(92.dp)) {
        val w = size.width
        val h = size.height
        val center = Offset(w / 2f, h / 2f)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    AntigravityColors.AccentBlue.copy(alpha = glowAlpha),
                    Color.Transparent,
                ),
                center = center,
                radius = w * 0.56f,
            ),
            radius = w * 0.56f,
            center = center,
        )

        val arch = Path().apply {
            moveTo(w * 0.50f, h * 0.16f)
            cubicTo(w * 0.60f, h * 0.16f, w * 0.67f, h * 0.34f, w * 0.77f, h * 0.76f)
            cubicTo(w * 0.79f, h * 0.84f, w * 0.72f, h * 0.87f, w * 0.67f, h * 0.80f)
            cubicTo(w * 0.60f, h * 0.68f, w * 0.56f, h * 0.62f, w * 0.50f, h * 0.62f)
            cubicTo(w * 0.44f, h * 0.62f, w * 0.40f, h * 0.68f, w * 0.33f, h * 0.80f)
            cubicTo(w * 0.28f, h * 0.87f, w * 0.21f, h * 0.84f, w * 0.23f, h * 0.76f)
            cubicTo(w * 0.33f, h * 0.34f, w * 0.40f, h * 0.16f, w * 0.50f, h * 0.16f)
            close()
        }

        drawPath(
            path = arch,
            brush = Brush.verticalGradient(
                colors = listOf(
                    AntigravityColors.AccentCyan,
                    AntigravityColors.AccentBlue,
                    AntigravityColors.AccentViolet,
                ),
                startY = h * 0.16f,
                endY = h * 0.86f,
            ),
        )
    }
}
