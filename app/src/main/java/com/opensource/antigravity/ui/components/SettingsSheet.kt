package com.opensource.antigravity.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.opensource.antigravity.data.AppSettings
import com.opensource.antigravity.data.ThemeMode
import com.opensource.antigravity.ui.theme.LocalAntigravityPalette

data class SettingsSheetActions(
    val onDismiss: () -> Unit,
    val onToggleNotifications: (Boolean) -> Unit,
    val onToggleMobileOptimization: (Boolean) -> Unit,
    val onToggleDesktopMode: (Boolean) -> Unit,
    val onToggleKeepScreenOn: (Boolean) -> Unit,
    val onSelectTheme: (ThemeMode) -> Unit,
    val onClearCacheOnly: () -> Unit,
    val onSignOut: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    settings: AppSettings,
    isOnline: Boolean,
    actions: SettingsSheetActions,
) {
    val palette = LocalAntigravityPalette.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = actions.onDismiss,
        sheetState = sheetState,
        containerColor = palette.surfaceElevated,
        contentColor = palette.inkPrimary,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Antigravity Remote",
                    style = MaterialTheme.typography.headlineSmall,
                    color = palette.inkPrimary,
                )
                StatusPill(isOnline = isOnline)
            }

            HorizontalDivider(color = palette.borderHairline)

            SettingToggleRow(
                title = "Notificaciones nativas",
                subtitle = "Convierte alertas web en notificaciones Android limpias",
                checked = settings.notificationsEnabled,
                onCheckedChange = actions.onToggleNotifications,
            )
            SettingToggleRow(
                title = "Optimización táctil móvil",
                subtitle = "Ajusta viewport, scroll e inputs para pantalla móvil",
                checked = settings.mobileOptimization,
                onCheckedChange = actions.onToggleMobileOptimization,
            )
            SettingToggleRow(
                title = "Modo escritorio",
                subtitle = "Vista amplia si necesitas inspeccionar paneles laterales",
                checked = settings.desktopMode,
                onCheckedChange = actions.onToggleDesktopMode,
            )
            SettingToggleRow(
                title = "Mantener pantalla encendida",
                subtitle = "Evita suspensión mientras supervisas agentes remotos",
                checked = settings.keepScreenOn,
                onCheckedChange = actions.onToggleKeepScreenOn,
            )

            ThemeModeSelector(
                selected = settings.themeMode,
                onSelect = actions.onSelectTheme,
            )

            HorizontalDivider(color = palette.borderHairline)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedButton(
                    onClick = actions.onClearCacheOnly,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = palette.inkPrimary),
                ) {
                    Text(text = "Limpiar caché")
                }
                OutlinedButton(
                    onClick = actions.onSignOut,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = palette.danger),
                ) {
                    Text(text = "Cerrar sesión")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun StatusPill(isOnline: Boolean) {
    val palette = LocalAntigravityPalette.current
    val dotColor = if (isOnline) palette.onlineDot else palette.danger
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(palette.canvas)
            .border(1.dp, palette.borderHairline, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor),
        )
        Text(
            text = if (isOnline) "Conectado" else "Sin conexión",
            style = MaterialTheme.typography.labelMedium,
            color = palette.inkSecondary,
        )
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val palette = LocalAntigravityPalette.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = palette.inkPrimary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = palette.inkSecondary,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = palette.canvas,
                checkedTrackColor = palette.accentStart,
            ),
        )
    }
}

@Composable
private fun ThemeModeSelector(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
) {
    val palette = LocalAntigravityPalette.current
    val options = listOf(
        ThemeMode.DARK to "Oscuro",
        ThemeMode.LIGHT to "Claro",
        ThemeMode.SYSTEM to "Sistema",
    )
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Apariencia",
            style = MaterialTheme.typography.titleMedium,
            color = palette.inkPrimary,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            options.forEach { (mode, label) ->
                val isSelected = mode == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) palette.accentStart.copy(alpha = 0.22f) else palette.canvas)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) palette.accentStart else palette.borderHairline,
                            shape = RoundedCornerShape(10.dp),
                        )
                        .clickable { onSelect(mode) }
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        ),
                        color = if (isSelected) palette.inkPrimary else palette.inkSecondary,
                    )
                }
            }
        }
    }
}
