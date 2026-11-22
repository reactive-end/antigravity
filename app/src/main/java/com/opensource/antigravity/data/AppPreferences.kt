package com.opensource.antigravity.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "antigravity_settings",
)

enum class ThemeMode(val storageValue: String) {
    DARK("dark"),
    LIGHT("light"),
    SYSTEM("system");

    companion object {
        fun fromStorage(raw: String?): ThemeMode =
            entries.firstOrNull { it.storageValue == raw } ?: DARK
    }
}

data class AppSettings(
    val notificationsEnabled: Boolean = true,
    val mobileOptimization: Boolean = true,
    val desktopMode: Boolean = false,
    val keepScreenOn: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.DARK,
    val lastVisitedUrl: String = DEFAULT_REMOTE_URL,
) {
    companion object {
        const val DEFAULT_REMOTE_URL = "https://antigravity.google.com/remote"
    }
}

class AppPreferencesRepository(private val context: Context) {

    private object Keys {
        val NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
        val MOBILE_OPTIMIZATION = booleanPreferencesKey("mobile_optimization")
        val DESKTOP_MODE = booleanPreferencesKey("desktop_mode")
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LAST_URL = stringPreferencesKey("last_visited_url")
    }

    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { prefs ->
        AppSettings(
            notificationsEnabled = prefs[Keys.NOTIFICATIONS] ?: true,
            mobileOptimization = prefs[Keys.MOBILE_OPTIMIZATION] ?: true,
            desktopMode = prefs[Keys.DESKTOP_MODE] ?: false,
            keepScreenOn = prefs[Keys.KEEP_SCREEN_ON] ?: true,
            themeMode = ThemeMode.fromStorage(prefs[Keys.THEME_MODE]),
            lastVisitedUrl = prefs[Keys.LAST_URL] ?: AppSettings.DEFAULT_REMOTE_URL,
        )
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.NOTIFICATIONS] = enabled }
    }

    suspend fun setMobileOptimization(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.MOBILE_OPTIMIZATION] = enabled }
    }

    suspend fun setDesktopMode(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.DESKTOP_MODE] = enabled }
    }

    suspend fun setKeepScreenOn(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.KEEP_SCREEN_ON] = enabled }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { it[Keys.THEME_MODE] = mode.storageValue }
    }

    suspend fun setLastVisitedUrl(url: String) {
        context.settingsDataStore.edit { it[Keys.LAST_URL] = url }
    }
}
