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
        assertTrue(css.contains("--card: #1E1F20"))
        assertTrue(css.contains("--ga-control-height-sm: 40px"))
        assertTrue(css.contains("--ga-control-height-md: 44px"))
        assertTrue(css.contains("div.surface-composer-card"))
        assertTrue(css.contains(".agy-button-icon-sm"))
        assertTrue(css.contains(".agy-send-button"))
        assertTrue(css.contains("header-feedback-btn--fullscreen"))
        assertTrue(css.contains("Select model"))
        assertTrue(css.contains("backdrop-filter: none !important"))
        assertFalse(css.contains("a.gb_C"))

        val js = MobileBridgeScript.INJECT_SCRIPT
        assertTrue(js.contains("antigravity-mobile-polish"))
        assertFalse(js.contains("\${"))
    }
}
