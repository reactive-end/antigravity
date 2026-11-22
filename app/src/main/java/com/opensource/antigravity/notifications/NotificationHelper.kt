package com.opensource.antigravity.notifications

import android.Manifest
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

    private val nextId = AtomicInteger(1000)

    fun ensureChannel() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Notificaciones nativas del espacio remoto de Antigravity"
            enableVibration(true)
        }
        manager.createNotificationChannel(channel)
    }

    fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun postNativeNotification(title: String, body: String) {
        if (!hasNotificationPermission()) return
        ensureChannel()

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val cleanTitle = title.trim().ifEmpty { "Antigravity" }
        val cleanBody = body.trim().ifEmpty { "Actualización disponible en tu sesión remota." }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(cleanTitle)
            .setContentText(cleanBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(cleanBody))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        runCatching {
            NotificationManagerCompat.from(context).notify(nextId.incrementAndGet(), notification)
        }
    }

    companion object {
        const val CHANNEL_ID = "antigravity_remote_alerts"
        const val CHANNEL_NAME = "Antigravity Remote"
    }
}
