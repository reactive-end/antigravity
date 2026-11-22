package com.opensource.antigravity.webview

import android.content.Context
import android.content.MutableContextWrapper
import android.view.ViewGroup
import android.webkit.WebView

/**
 * Retains a single WebView across Activity background/foreground transitions so leaving
 * the app never destroys the active Antigravity Remote session or pauses JS notifications.
 */
object WebViewHost {

    @Volatile
    var isInBackground: Boolean = false

    @Volatile
    private var contextWrapper: MutableContextWrapper? = null

    @Volatile
    private var cachedWebView: WebView? = null

    fun acquire(
        activityContext: Context,
        creator: (Context) -> WebView,
    ): WebView {
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

    fun detachFromActivity(appContext: Context) {
        contextWrapper?.baseContext = appContext.applicationContext
        val view = cachedWebView ?: return
        (view.parent as? ViewGroup)?.removeView(view)
        view.resumeTimers()
    }

    fun keepAliveTick() {
        val view = cachedWebView ?: return
        runCatching {
            view.onResume()
            view.resumeTimers()
        }
    }
}
