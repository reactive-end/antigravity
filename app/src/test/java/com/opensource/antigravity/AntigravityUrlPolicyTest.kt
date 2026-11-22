package com.opensource.antigravity

import com.opensource.antigravity.webview.AntigravityUrlPolicy
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AntigravityUrlPolicyTest {

    @Test
    fun remoteAndGoogleAuthHostsStayInApp() {
        assertTrue(AntigravityUrlPolicy.shouldLoadInApp("https://antigravity.google.com/remote"))
        assertTrue(AntigravityUrlPolicy.shouldLoadInApp("https://accounts.google.com/v3/signin"))
        assertTrue(AntigravityUrlPolicy.shouldLoadInApp("https://myaccount.google.com/security"))
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
}
