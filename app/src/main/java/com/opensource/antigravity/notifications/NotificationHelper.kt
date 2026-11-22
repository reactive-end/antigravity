package com.opensource.antigravity.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.opensource.antigravity.MainActivity
import com.opensource.antigravity.R
import java.util.concurrent.atomic.AtomicInteger

class NotificationHelper(private val context: Context) {

    private val nextId = AtomicInteger(2000)

    fun ensureChannels() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        runCatching {
            manager.cancel(LEGACY_KEEPALIVE_NOTIFICATION_ID)
            manager.deleteNotificationChannel(LEGACY_KEEPALIVE_CHANNEL_ID)
        }

        val alertsChannel = NotificationChannel(
            CHANNEL_ALERTS_ID,
            CHANNEL_ALERTS_NAME,
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Avisos cuando una tarea de Antigravity termina o requiere atención"
            enableVibration(true)
        }
        manager.createNotificationChannel(alertsChannel)
    }

    fun hasRuntimePermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun areNotificationsFullyEnabled(): Boolean {
        if (!hasRuntimePermission()) return false
        val compat = NotificationManagerCompat.from(context)
        if (!compat.areNotificationsEnabled()) return false
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val ch = manager.getNotificationChannel(CHANNEL_ALERTS_ID)
        return ch == null || ch.importance != NotificationManager.IMPORTANCE_NONE
    }

    fun buildNotificationSettingsIntent(): Intent {
        return Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.takeIf { it.resolveActivity(context.packageManager) != null }
            ?: Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null),
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
    }

    fun postNativeNotification(title: String, body: String) {
        if (!areNotificationsFullyEnabled()) return
        ensureChannels()

        val cleanTitle = title.trim().ifEmpty { "Antigravity" }
        val cleanBody = body.trim().ifEmpty { "El agente terminó de responder." }

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS_ID)
            .setSmallIcon(R.drawable.ic_stat_antigravity)
            .setContentTitle(cleanTitle)
            .setContentText(cleanBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(cleanBody))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        runCatching {
            NotificationManagerCompat.from(context).notify(nextId.incrementAndGet(), notification)
        }
    }

    companion object {
        const val CHANNEL_ALERTS_ID = "antigravity_remote_alerts"
        const val CHANNEL_ALERTS_NAME = "Antigravity Remote"
        private const val LEGACY_KEEPALIVE_CHANNEL_ID = "antigravity_background_sync"
        private const val LEGACY_KEEPALIVE_NOTIFICATION_ID = 1001
    }
}
