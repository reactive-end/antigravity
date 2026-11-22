package com.opensource.antigravity

import com.opensource.antigravity.webview.AntigravityUrlPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AntigravityUrlPolicyTest {

    @Test
    fun remoteAndAllRegionalGoogleAuthHostsStayInApp() {
        assertTrue(AntigravityUrlPolicy.shouldLoadInApp("https://antigravity.google.com/remote"))
        assertTrue(AntigravityUrlPolicy.shouldLoadInApp("https://accounts.google.com/v3/signin"))
        assertTrue(AntigravityUrlPolicy.shouldLoadInApp("https://accounts.google.co.ve/CheckCookie?continue=https%3A%2F%2Fantigravity.google.com%2Fremote"))
        assertTrue(AntigravityUrlPolicy.shouldLoadInApp("https://www.google.co.ve/accounts/SetOSID"))
        assertTrue(AntigravityUrlPolicy.shouldLoadInApp("https://accounts.google.com.co/v3/signin/challenge"))
        assertTrue(AntigravityUrlPolicy.shouldLoadInApp("https://accounts.google.es/CheckCookie"))
        assertTrue(AntigravityUrlPolicy.shouldLoadInApp("https://www.google.com/accounts/ServiceLogin"))
        assertTrue(AntigravityUrlPolicy.shouldLoadInApp("https://myaccount.google.com/security"))
        assertTrue(AntigravityUrlPolicy.shouldLoadInApp("https://antigravity-pa.googleapis.com/v1/session"))
        assertTrue(AntigravityUrlPolicy.shouldLoadInApp("https://antigravity.static.usercontent.goog/web/2.34.0/?iframe=true"))
    }

    @Test
    fun intentFallbackUrlIsExtractedForInAppLoad() {
        val intentUrl = "intent://accounts.google.com/CheckCookie#Intent;scheme=https;" +
            "S.browser_fallback_url=https%3A%2F%2Faccounts.google.co.ve%2FCheckCookie%3Fhl%3Des;end"
        assertEquals(
            "https://accounts.google.co.ve/CheckCookie?hl=es",
            AntigravityUrlPolicy.extractIntentFallbackUrl(intentUrl),
        )
    }

    @Test
    fun externalHostsOpenOutsideApp() {
        assertFalse(AntigravityUrlPolicy.shouldLoadInApp("https://github.com/reactive-end/anvil"))
        assertFalse(AntigravityUrlPolicy.shouldLoadInApp("mailto:test@example.com"))
    }

    @Test
    fun userAgentStripsWebViewTokenForGoogleSignIn() {
        val raw = "Mozilla/5.0 (Linux; Android 15; Pixel 8; wv) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Version/4.0 Chrome/134.0.0.0 Mobile Safari/537.36"
        val clean = AntigravityUrlPolicy.sanitizeUserAgent(raw)
        assertFalse(clean.contains("; wv"))
        assertFalse(clean.contains("Version/4.0"))
        assertTrue(clean.contains("Chrome/134.0.0.0"))
    }

    @Test
    fun documentStartOriginsCoverRemoteHostAndUserContentIframe() {
        val origins = AntigravityUrlPolicy.DOCUMENT_START_ORIGINS
        assertTrue(origins.contains("https://antigravity.google.com"))
        assertTrue(origins.contains("https://antigravity.static.usercontent.goog"))
        assertTrue(origins.contains("https://*.usercontent.goog"))
        assertFalse(origins.contains("*"))
    }

    @Test
    fun trustedMediaOriginsAllowAntigravityAndUserContentOnly() {
        assertTrue(AntigravityUrlPolicy.isTrustedMediaOrigin("antigravity.google.com"))
        assertTrue(AntigravityUrlPolicy.isTrustedMediaOrigin("antigravity.static.usercontent.goog"))
        assertFalse(AntigravityUrlPolicy.isTrustedMediaOrigin("evil.example.com"))
        assertFalse(AntigravityUrlPolicy.isTrustedMediaOrigin(null))
    }
}
