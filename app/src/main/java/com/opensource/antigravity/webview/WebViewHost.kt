package com.opensource.antigravity.webview

import android.content.Context
import android.content.MutableContextWrapper
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView

/**
 * Custom WebView that keeps Chromium's network & V8 engine active when the user switches
 * to another app, without requiring a persistent Foreground Service notification.
 */
class AlwaysActiveWebView(context: Context) : WebView(context) {
    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(View.VISIBLE)
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

    private const val BG_TICK_INTERVAL_MS = 2_000L
    private const val MAX_BG_TICKS = 900 // 30 minutes max background watch
}
