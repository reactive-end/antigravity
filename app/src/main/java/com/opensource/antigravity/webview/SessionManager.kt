package com.opensource.antigravity.webview

import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView

object SessionManager {

    fun configurePersistentCookies(webView: WebView) {
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)
    }

    fun flush() {
        runCatching { CookieManager.getInstance().flush() }
    }

    fun clearCachePreservingSession(webView: WebView) {
        webView.clearCache(true)
        webView.clearFormData()
        flush()
        webView.reload()
    }

    fun signOut(webView: WebView, onDone: () -> Unit) {
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies {
            cookieManager.flush()
            WebStorage.getInstance().deleteAllData()
            webView.clearCache(true)
            webView.clearHistory()
            webView.loadUrl(AntigravityUrlPolicy.REMOTE_HOME_URL)
            onDone()
        }
    }
}
