package com.opensource.antigravity.webview

import java.net.URI

object AntigravityUrlPolicy {

    const val REMOTE_HOME_URL = "https://antigravity.google.com/remote"

    private val inAppHosts = setOf(
        "antigravity.google.com",
        "antigravity.google",
        "accounts.google.com",
        "accounts.youtube.com",
        "myaccount.google.com",
        "gds.google.com",
        "signin.google.com",
        "oauth2.googleapis.com",
        "consent.google.com",
    )

    private val inAppSuffixes = listOf(
        ".antigravity.google.com",
        ".antigravity.google",
        ".accounts.google.com",
    )

    fun shouldLoadInApp(rawUrl: String): Boolean {
        val uri = runCatching { URI(rawUrl.trim()) }.getOrNull() ?: return false
        val scheme = uri.scheme?.lowercase() ?: return false
        if (scheme != "https" && scheme != "http") return false
        val host = uri.host?.lowercase() ?: return false
        if (host in inAppHosts) return true
        return inAppSuffixes.any { host.endsWith(it) }
    }

    fun isAntigravityWorkspaceUrl(rawUrl: String): Boolean {
        val host = runCatching { URI(rawUrl.trim()).host?.lowercase() }.getOrNull() ?: return false
        return host == "antigravity.google.com" ||
            host == "antigravity.google" ||
            host.endsWith(".antigravity.google.com")
    }

    fun sanitizeUserAgent(defaultUserAgent: String): String {
        return defaultUserAgent
            .replace("; wv)", ")")
            .replace("; wv;", ";")
            .replace(Regex("Version/\\d+\\.\\d+\\s*"), "")
            .replace(Regex("\\s{2,}"), " ")
            .trim()
    }
}
