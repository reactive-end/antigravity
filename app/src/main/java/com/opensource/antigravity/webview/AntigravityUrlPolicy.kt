package com.opensource.antigravity.webview

import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

object AntigravityUrlPolicy {

    const val REMOTE_HOME_URL = "https://antigravity.google.com/remote"

    val DOCUMENT_START_ORIGINS: Set<String> = setOf(
        "https://antigravity.google.com",
        "https://*.antigravity.google.com",
        "https://antigravity.static.usercontent.goog",
        "https://*.usercontent.goog",
    )

    // Covers google.com, accounts.google.com, accounts.google.co.ve, www.google.com.co,
    // accounts.google.es, antigravity.google.com, myaccount.google.com, etc.
    private val googleRegionalHostRegex = Regex("^([a-z0-9-]+\\.)*google(\\.[a-z]{2,3}){1,2}$")

    private val inAppExactHosts = setOf(
        "antigravity.google",
        "google.com",
        "googleapis.com",
        "gstatic.com",
        "googleusercontent.com",
        "youtube.com",
        "withgoogle.com",
    )

    private val inAppSuffixes = listOf(
        ".google",
        ".googleapis.com",
        ".gstatic.com",
        ".googleusercontent.com",
        ".usercontent.goog",
        ".youtube.com",
        ".gvt1.com",
        ".gvt2.com",
        ".withgoogle.com",
    )

    fun shouldLoadInApp(rawUrl: String): Boolean {
        val uri = runCatching { URI(rawUrl.trim()) }.getOrNull() ?: return false
        val scheme = uri.scheme?.lowercase() ?: return false
        if (scheme != "https" && scheme != "http") return false
        val host = uri.host?.lowercase() ?: return false
        if (host in inAppExactHosts) return true
        if (googleRegionalHostRegex.matches(host)) return true
        return inAppSuffixes.any { host.endsWith(it) }
    }

    fun isHttpOrHttps(rawUrl: String): Boolean {
        val scheme = runCatching { URI(rawUrl.trim()).scheme?.lowercase() }.getOrNull()
        return scheme == "https" || scheme == "http"
    }

    fun extractIntentFallbackUrl(rawUrl: String): String? {
        if (!rawUrl.startsWith("intent://", ignoreCase = true)) return null
        val match = Regex("S\\.browser_fallback_url=([^;]+)").find(rawUrl) ?: return null
        return runCatching {
            URLDecoder.decode(match.groupValues[1], StandardCharsets.UTF_8.name())
        }.getOrNull()?.takeIf { isHttpOrHttps(it) }
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
