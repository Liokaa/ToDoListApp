package com.example.todolist

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        val KEY_NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
        val KEY_LANGUAGE = stringPreferencesKey("app_language") // "es", "en", "ja"
    }

    // Obtener preferencia de modo oscuro (por defecto modo claro = false)
    val darkModeFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_DARK_MODE] ?: false
    }

    // Obtener preferencia de notificaciones (por defecto activadas = true)
    val notificationsFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_NOTIFICATIONS] ?: true
    }

    // Obtener preferencia de idioma (por defecto español = "es")
    val languageFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_LANGUAGE] ?: "es"
    }

    // Guardar selección de modo oscuro
    suspend fun saveDarkMode(isDark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DARK_MODE] = isDark
        }
    }

    // Guardar preferencia de notificaciones
    suspend fun saveNotifications(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_NOTIFICATIONS] = enabled
        }
    }

    // Guardar preferencia de idioma ("es", "en", "ja")
    suspend fun saveLanguage(languageCode: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = languageCode
        }
    }
}