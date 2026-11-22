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

    companion object {
        private const val PREFS_NAME = "antigravity_session"
        private const val KEY_LAST_URL = "last_visited_url"
    }
}
