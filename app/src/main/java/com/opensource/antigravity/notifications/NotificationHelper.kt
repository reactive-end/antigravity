package com.opensource.antigravity.notifications

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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

        val alertsChannel = NotificationChannel(
            CHANNEL_ALERTS_ID,
            CHANNEL_ALERTS_NAME,
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Avisos cuando una tarea de Antigravity termina o requiere atención"
            enableVibration(true)
        }

        val syncChannel = NotificationChannel(
            CHANNEL_KEEPALIVE_ID,
            CHANNEL_KEEPALIVE_NAME,
            NotificationManager.IMPORTANCE_MIN,
        ).apply {
            description = "Mantiene activa la conexión remota en segundo plano para recibir avisos"
            setShowBadge(false)
            setSound(null, null)
            enableVibration(false)
        }

        manager.createNotificationChannel(alertsChannel)
        manager.createNotificationChannel(syncChannel)
    }

    fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun buildKeepAliveNotification(): Notification {
        ensureChannels()
        return NotificationCompat.Builder(context, CHANNEL_KEEPALIVE_ID)
            .setSmallIcon(R.drawable.ic_stat_antigravity)
            .setContentTitle("Antigravity conectado")
            .setContentText("Escuchando notificaciones remotas en segundo plano")
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(createLaunchPendingIntent())
            .build()
    }

    fun postNativeNotification(title: String, body: String) {
        if (!hasNotificationPermission()) return
        ensureChannels()

        val cleanTitle = title.trim().ifEmpty { "Antigravity" }
        val cleanBody = body.trim().ifEmpty { "Una tarea terminó o requiere tu atención." }

        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS_ID)
            .setSmallIcon(R.drawable.ic_stat_antigravity)
            .setContentTitle(cleanTitle)
            .setContentText(cleanBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(cleanBody))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(createLaunchPendingIntent())
            .build()

        runCatching {
            NotificationManagerCompat.from(context).notify(nextId.incrementAndGet(), notification)
        }
    }

    private fun createLaunchPendingIntent(): PendingIntent {
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        const val CHANNEL_ALERTS_ID = "antigravity_remote_alerts"
        const val CHANNEL_ALERTS_NAME = "Antigravity Remote"
        const val CHANNEL_KEEPALIVE_ID = "antigravity_background_sync"
        const val CHANNEL_KEEPALIVE_NAME = "Conexión en segundo plano"
        const val KEEPALIVE_NOTIFICATION_ID = 1001
    }
}
