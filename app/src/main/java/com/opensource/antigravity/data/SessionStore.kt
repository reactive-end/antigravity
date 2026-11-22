package com.opensource.antigravity.data

import android.content.Context
import com.opensource.antigravity.webview.AntigravityUrlPolicy

class SessionStore(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getLastVisitedUrl(): String {
        val saved = prefs.getString(KEY_LAST_URL, null)?.trim().orEmpty()
        return if (AntigravityUrlPolicy.isAntigravityWorkspaceUrl(saved)) {
            saved
        } else {
            AntigravityUrlPolicy.REMOTE_HOME_URL
        }
    }

    fun saveLastVisitedUrl(url: String) {
        if (!AntigravityUrlPolicy.isAntigravityWorkspaceUrl(url)) return
        prefs.edit().putString(KEY_LAST_URL, url).apply()
    }

    fun isNotifSelfTestDone(): Boolean =
        prefs.getBoolean(KEY_NOTIF_TEST_DONE, false)

    fun markNotifSelfTestDone() {
        prefs.edit().putBoolean(KEY_NOTIF_TEST_DONE, true).apply()
    }

    companion object {
        private const val PREFS_NAME = "antigravity_session"
        private const val KEY_LAST_URL = "last_visited_url"
        private const val KEY_NOTIF_TEST_DONE = "notif_self_test_v2"
    }
}
