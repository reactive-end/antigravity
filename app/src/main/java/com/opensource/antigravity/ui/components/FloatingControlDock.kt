package com.opensource.antigravity.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.opensource.antigravity.ui.theme.LocalAntigravityPalette

@Composable
fun CosmicProgressBar(
    progress: Int,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAntigravityPalette.current
    val visible = progress in 1..99
    val animatedFraction by animateFloatAsState(
        targetValue = (progress / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "progress_fraction",
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(120)),
        exit = fadeOut(tween(260)),
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.5.dp)
                .background(Color.Transparent),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedFraction)
                    .height(2.5.dp)
                    .background(palette.cosmicBrush),
            )
        }
    }
}

@Composable
fun FloatingControlDock(
    isOnline: Boolean,
    canGoBack: Boolean,
    onBack: () -> Unit,
    onReload: () -> Unit,
    onHome: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = LocalAntigravityPalette.current
    var expanded by remember { mutableStateOf(false) }

    val shape = RoundedCornerShape(24.dp)
    Row(
        modifier = modifier
            .shadow(elevation = 10.dp, shape = shape, clip = false)
            .clip(shape)
            .background(palette.surfaceGlass)
            .border(width = 1.dp, color = palette.borderHairline, shape = shape)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
            )
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (expanded) {
            if (canGoBack) {
                DockActionIcon(
                    description = "Atrás",
                    onClick = {
                        onBack()
                        expanded = false
                    },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Atrás",
                        tint = palette.inkPrimary,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
            DockActionIcon(
                description = "Recargar",
                onClick = {
                    onReload()
                    expanded = false
                },
            ) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = "Recargar",
                    tint = palette.inkPrimary,
                    modifier = Modifier.size(18.dp),
                )
            }
            DockActionIcon(
                description = "Inicio remoto",
                onClick = {
                    onHome()
                    expanded = false
                },
            ) {
                Icon(
                    imageVector = Icons.Rounded.Home,
                    contentDescription = "Inicio remoto",
                    tint = palette.inkPrimary,
                    modifier = Modifier.size(18.dp),
                )
            }
            DockActionIcon(
                description = "Ajustes",
                onClick = {
                    expanded = false
                    onOpenSettings()
                },
            ) {
                Icon(
                    imageVector = Icons.Rounded.Tune,
                    contentDescription = "Ajustes",
                    tint = palette.accentEnd,
                    modifier = Modifier.size(18.dp),
                )
            }
            DockActionIcon(
                description = "Contraer barra",
                onClick = { expanded = false },
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Contraer barra",
                    tint = palette.inkSecondary,
                    modifier = Modifier.size(16.dp),
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { expanded = true }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .semantics { contentDescription = "Controles de Antigravity" },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isOnline) palette.onlineDot else palette.danger),
                )
                Icon(
                    imageVector = Icons.Rounded.Tune,
                    contentDescription = "Controles de Antigravity",
                    tint = palette.inkPrimary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun DockActionIcon(
    description: String,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(36.dp)
            .semantics { contentDescription = description },
        content = content,
    )
}
