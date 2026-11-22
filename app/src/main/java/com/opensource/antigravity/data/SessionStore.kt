package com.opensource.antigravity.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.opensource.antigravity.webview.AntigravityUrlPolicy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "antigravity_session",
)

class SessionStore(private val context: Context) {

    private val lastUrlKey = stringPreferencesKey("last_visited_url")

    suspend fun getLastVisitedUrl(): String {
        val saved = context.sessionDataStore.data.map { it[lastUrlKey] }.first()
        return saved?.takeIf { AntigravityUrlPolicy.isAntigravityWorkspaceUrl(it) }
            ?: AntigravityUrlPolicy.REMOTE_HOME_URL
    }

    suspend fun saveLastVisitedUrl(url: String) {
        if (!AntigravityUrlPolicy.isAntigravityWorkspaceUrl(url)) return
        context.sessionDataStore.edit { it[lastUrlKey] = url }
    }
}
