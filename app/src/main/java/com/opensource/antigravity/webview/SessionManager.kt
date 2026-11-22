package com.opensource.antigravity.webview

import android.webkit.CookieManager
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
}
