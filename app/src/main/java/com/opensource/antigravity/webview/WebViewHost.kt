package com.opensource.antigravity.webview

import android.content.Context
import android.content.MutableContextWrapper
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView

/**
 * Custom WebView that reports permanent foreground visibility and focus to Chromium so
 * switching apps never disconnects the live WebSocket/gRPC stream to the remote PC instance.
 */
class AlwaysActiveWebView(context: Context) : WebView(context) {
    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(View.VISIBLE)
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, View.VISIBLE)
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(true)
    }
}

object WebViewHost {

    @Volatile
    var isInBackground: Boolean = false
        private set

    private val mainHandler = Handler(Looper.getMainLooper())
    private var bgTickCount = 0

    @Volatile
    private var contextWrapper: MutableContextWrapper? = null

    @Volatile
    private var cachedWebView: AlwaysActiveWebView? = null

    private var wakeLock: PowerManager.WakeLock? = null
    private var wifiLock: WifiManager.WifiLock? = null

    private val bgTicker = object : Runnable {
        override fun run() {
            if (!isInBackground) return
            val view = cachedWebView ?: return
            runCatching {
                view.onResume()
                view.resumeTimers()
                view.evaluateJavascript(
                    "if(window.__AGY_BG_TICK){window.__AGY_BG_TICK();}",
                    null,
                )
            }
            bgTickCount++
            if (bgTickCount < MAX_BG_TICKS) {
                mainHandler.postDelayed(this, BG_TICK_INTERVAL_MS)
            } else {
                releaseBackgroundLocks()
            }
        }
    }

    fun acquire(
        activityContext: Context,
        creator: (Context) -> AlwaysActiveWebView,
    ): AlwaysActiveWebView {
        val existing = cachedWebView
        if (existing != null) {
            contextWrapper?.baseContext = activityContext
            (existing.parent as? ViewGroup)?.removeView(existing)
            existing.onResume()
            existing.resumeTimers()
            return existing
        }

        val wrapper = MutableContextWrapper(activityContext)
        contextWrapper = wrapper
        return creator(wrapper).also { cachedWebView = it }
    }

    fun setBackgroundState(inBackground: Boolean) {
        isInBackground = inBackground
        mainHandler.removeCallbacks(bgTicker)
        val view = cachedWebView ?: return
        val appCtx = view.context.applicationContext

        if (inBackground) {
            acquireBackgroundLocks(appCtx)
        } else {
            releaseBackgroundLocks()
        }

        runCatching {
            view.onResume()
            view.resumeTimers()
            view.evaluateJavascript(MobileBridgeScript.INJECT_SCRIPT, null)
            view.evaluateJavascript(
                "if(window.__AGY_SET_BG){window.__AGY_SET_BG($inBackground);}",
                null,
            )
        }
        if (inBackground) {
            bgTickCount = 0
            mainHandler.postDelayed(bgTicker, BG_TICK_INTERVAL_MS)
        }
    }

    fun detachFromActivity(appContext: Context) {
        contextWrapper?.baseContext = appContext.applicationContext
        val view = cachedWebView ?: return
        (view.parent as? ViewGroup)?.removeView(view)
    }

    private fun acquireBackgroundLocks(appContext: Context) {
        runCatching {
            if (wakeLock == null) {
                val pm = appContext.getSystemService(Context.POWER_SERVICE) as PowerManager
                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Antigravity:RemoteStreamLock").apply {
                    setReferenceCounted(false)
                }
            }
            if (wakeLock?.isHeld == false) {
                wakeLock?.acquire(LOCK_TIMEOUT_MS)
            }

            if (wifiLock == null) {
                val wm = appContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val mode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    WifiManager.WIFI_MODE_FULL_LOW_LATENCY
                } else {
                    3 // WIFI_MODE_FULL_HIGH_PERF on API 26-28
                }
                wifiLock = wm.createWifiLock(mode, "Antigravity:RemoteWifiLock").apply {
                    setReferenceCounted(false)
                }
            }
            if (wifiLock?.isHeld == false) {
                wifiLock?.acquire()
            }
        }
    }

    private fun releaseBackgroundLocks() {
        runCatching {
            if (wakeLock?.isHeld == true) wakeLock?.release()
            if (wifiLock?.isHeld == true) wifiLock?.release()
        }
    }

    private const val BG_TICK_INTERVAL_MS = 1_500L
    private const val MAX_BG_TICKS = 600 // 15 minutes max background watch
    private const val LOCK_TIMEOUT_MS = 15 * 60 * 1_000L
}
