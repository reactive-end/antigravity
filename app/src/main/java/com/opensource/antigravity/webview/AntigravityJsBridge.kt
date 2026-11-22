package com.opensource.antigravity.webview

import android.webkit.JavascriptInterface
import com.opensource.antigravity.notifications.NotificationHelper
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

class AntigravityJsBridge(
    private val notificationHelper: NotificationHelper,
) {
    private val lastNotificationAtMs = AtomicLong(0L)
    private val lastPayloadKey = AtomicReference("")

    @JavascriptInterface
    fun isInBackground(): Boolean = WebViewHost.isInBackground

    @JavascriptInterface
    fun notify(title: String, body: String) {
        if (!WebViewHost.isInBackground) return
        val now = System.currentTimeMillis()
        val key = "${title.trim()}|${body.trim()}"
        val prevAt = lastNotificationAtMs.get()
        val prevKey = lastPayloadKey.get()
        if (key == prevKey && now - prevAt < DUPLICATE_WINDOW_MS) return
        if (now - prevAt < MIN_NOTIFICATION_GAP_MS) return

        lastNotificationAtMs.set(now)
        lastPayloadKey.set(key)
        notificationHelper.postNativeNotification(title, body)
    }

    companion object {
        const val BRIDGE_NAME = "AntigravityNativeBridge"
        private const val MIN_NOTIFICATION_GAP_MS = 800L
        private const val DUPLICATE_WINDOW_MS = 4_000L
    }
}
