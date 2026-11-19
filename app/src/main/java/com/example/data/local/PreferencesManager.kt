package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.AppLanguage
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.finSmartDataStore: DataStore<Preferences> by preferencesDataStore(name = "finsmart_prefs")

data class UserSettingsState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val simpleMode: Boolean = false,
    val textScale: Float = 1.0f,
    val highContrast: Boolean = false,
    val voiceSpeed: Float = 1.0f,
    val notificationsEnabled: Boolean = true,
    val offlineSavedModuleIds: Set<String> = setOf("BASIC_BANKING", "FRAUD_SAFETY")
)

class PreferencesManager(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val SIMPLE_MODE = booleanPreferencesKey("simple_mode")
        val TEXT_SCALE = floatPreferencesKey("text_scale")
        val HIGH_CONTRAST = booleanPreferencesKey("high_contrast")
        val VOICE_SPEED = floatPreferencesKey("voice_speed")
        val NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
        val OFFLINE_MODULES = stringSetPreferencesKey("offline_modules")
    }

    val settingsFlow: Flow<UserSettingsState> = context.finSmartDataStore.data.map { prefs ->
        val themeStr = prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name
        val langStr = prefs[Keys.LANGUAGE] ?: AppLanguage.ENGLISH.name
        UserSettingsState(
            themeMode = ThemeMode.entries.firstOrNull { it.name == themeStr } ?: ThemeMode.SYSTEM,
            language = AppLanguage.entries.firstOrNull { it.name == langStr } ?: AppLanguage.ENGLISH,
            simpleMode = prefs[Keys.SIMPLE_MODE] ?: false,
            textScale = prefs[Keys.TEXT_SCALE] ?: 1.0f,
            highContrast = prefs[Keys.HIGH_CONTRAST] ?: false,
            voiceSpeed = prefs[Keys.VOICE_SPEED] ?: 1.0f,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS] ?: true,
            offlineSavedModuleIds = prefs[Keys.OFFLINE_MODULES] ?: setOf("BASIC_BANKING", "FRAUD_SAFETY")
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.finSmartDataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.finSmartDataStore.edit { it[Keys.LANGUAGE] = language.name }
    }

    suspend fun setSimpleMode(enabled: Boolean) {
        context.finSmartDataStore.edit { it[Keys.SIMPLE_MODE] = enabled }
    }

    suspend fun setTextScale(scale: Float) {
        context.finSmartDataStore.edit { it[Keys.TEXT_SCALE] = scale.coerceIn(0.9f, 1.35f) }
    }

    suspend fun setHighContrast(enabled: Boolean) {
        context.finSmartDataStore.edit { it[Keys.HIGH_CONTRAST] = enabled }
    }

    suspend fun setVoiceSpeed(speed: Float) {
        context.finSmartDataStore.edit { it[Keys.VOICE_SPEED] = speed.coerceIn(0.75f, 1.4f) }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.finSmartDataStore.edit { it[Keys.NOTIFICATIONS] = enabled }
    }

    suspend fun toggleOfflineModule(moduleId: String) {
        context.finSmartDataStore.edit { prefs ->
            val current = prefs[Keys.OFFLINE_MODULES]?.toMutableSet() ?: mutableSetOf()
            if (current.contains(moduleId)) current.remove(moduleId) else current.add(moduleId)
            prefs[Keys.OFFLINE_MODULES] = current
        }
    }
}
