package com.opensource.antigravity

import com.opensource.antigravity.webview.MobileBridgeScript
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MobileBridgeScriptTest {

    @Test
    fun polishCssEnforcesComfortableTouchTargetsAndDisablesBackdropFilter() {
        val css = MobileBridgeScript.MOBILE_POLISH_CSS
        assertTrue(css.contains("--background: #000000"))
        assertTrue(css.contains("--color-background: #000000"))
        assertTrue(css.contains("--card: #1E1F20"))
        assertTrue(css.contains("--color-card: #1E1F20"))
        assertTrue(css.contains("--ga-control-height-sm: 40px"))
        assertTrue(css.contains("--ga-control-height-md: 44px"))
        assertTrue(css.contains("div.surface-composer-card"))
        assertTrue(css.contains(".agy-button-icon-sm"))
        assertTrue(css.contains(".agy-button-icon"))
        assertTrue(css.contains(".agy-send-button"))
        assertTrue(css.contains("header-feedback-btn--fullscreen"))
        assertTrue(css.contains("html.agy-in-conversation"))
        assertTrue(css.contains("button[aria-label=\"Back to home\"]"))
        assertTrue(css.contains("div.surface-composer-card img"))
        assertTrue(css.contains("[role=\"dialog\"]"))
        assertTrue(css.contains("Scroll to Bottom"))
        assertTrue(css.contains("Select model"))
        assertTrue(css.contains("backdrop-filter: none !important"))
        assertFalse(css.contains("a.gb_C"))
        assertFalse(css.contains("div.surface-composer-card button.rounded-full"))

        val js = MobileBridgeScript.INJECT_SCRIPT
        assertTrue(js.contains("antigravity-mobile-polish"))
        assertTrue(js.contains("dispatchBgToFrames"))
        assertTrue(js.contains("CONV_MODE"))
        assertTrue(js.contains("just now"))
        assertTrue(js.contains("MutationObserver"))
        assertFalse(js.contains("\${"))
    }
}
