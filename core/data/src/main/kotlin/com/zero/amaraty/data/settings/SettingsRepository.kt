package com.zero.amaraty.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.amaratyPreferences: DataStore<Preferences> by preferencesDataStore(name = "amaraty_settings")

class SettingsRepository @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val store = context.amaratyPreferences

    val settings: Flow<AppSettings> = store.data.map { preferences ->
        AppSettings(
            useArabicDigits = preferences[USE_ARABIC_DIGITS] ?: false,
            darkTheme = preferences[DARK_THEME] ?: false,
            appLockEnabled = preferences[APP_LOCK_ENABLED] ?: false,
            secureScreens = preferences[SECURE_SCREENS] ?: false,
        )
    }

    suspend fun setArabicDigits(enabled: Boolean) {
        store.edit { it[USE_ARABIC_DIGITS] = enabled }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        store.edit { it[DARK_THEME] = enabled }
    }

    suspend fun setAppLock(enabled: Boolean) {
        store.edit { it[APP_LOCK_ENABLED] = enabled }
    }

    suspend fun setSecureScreens(enabled: Boolean) {
        store.edit { it[SECURE_SCREENS] = enabled }
    }

    private companion object {
        val USE_ARABIC_DIGITS = booleanPreferencesKey("use_arabic_digits")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        val SECURE_SCREENS = booleanPreferencesKey("secure_screens")
    }
}
