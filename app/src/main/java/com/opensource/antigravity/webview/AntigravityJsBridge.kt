package com.opensource.antigravity.webview

import android.webkit.JavascriptInterface
import com.opensource.antigravity.notifications.NotificationHelper
import java.util.concurrent.atomic.AtomicLong

class AntigravityJsBridge(
    private val notificationHelper: NotificationHelper,
) {
    private val lastNotificationAtMs = AtomicLong(0L)

    @JavascriptInterface
    fun notify(title: String, body: String) {
        val now = System.currentTimeMillis()
        val prev = lastNotificationAtMs.get()
        if (now - prev < MIN_NOTIFICATION_GAP_MS) return
        if (lastNotificationAtMs.compareAndSet(prev, now)) {
            notificationHelper.postNativeNotification(title, body)
        }
    }

    companion object {
        const val BRIDGE_NAME = "AntigravityNativeBridge"
        private const val MIN_NOTIFICATION_GAP_MS = 1_500L
    }
}
