package com.example.fridgetracker.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "user_settings")

data class UserSettings(
    val darkTheme: Boolean = false,
    val notificationsEnabled: Boolean = false,
    val recommendationsEnabled: Boolean = true,
    val expirationWarningsEnabled: Boolean = true,
    val cardGrid: Boolean = false,
    val titleTextSize: Float = 18f,
    val secondaryTextSize: Float = 14f
)

class UserSettingsRepository(private val context: Context) {
    private object Keys {
        val darkTheme = booleanPreferencesKey("dark_theme")
        val notifications = booleanPreferencesKey("notifications")
        val recommendations = booleanPreferencesKey("recommendations")
        val expirationWarnings = booleanPreferencesKey("expiration_warnings")
        val cardGrid = booleanPreferencesKey("card_grid")
        val titleTextSize = floatPreferencesKey("title_text_size")
        val secondaryTextSize = floatPreferencesKey("secondary_text_size")
    }

    val settings: Flow<UserSettings> = context.settingsDataStore.data.map { preferences ->
        UserSettings(
            darkTheme = preferences[Keys.darkTheme] ?: false,
            notificationsEnabled = preferences[Keys.notifications] ?: false,
            recommendationsEnabled = preferences[Keys.recommendations] ?: true,
            expirationWarningsEnabled = preferences[Keys.expirationWarnings] ?: true,
            cardGrid = preferences[Keys.cardGrid] ?: false,
            titleTextSize = preferences[Keys.titleTextSize] ?: 18f,
            secondaryTextSize = preferences[Keys.secondaryTextSize] ?: 14f
        )
    }

    suspend fun setDarkTheme(value: Boolean) = context.settingsDataStore.edit { it[Keys.darkTheme] = value }
    suspend fun setNotifications(value: Boolean) = context.settingsDataStore.edit { it[Keys.notifications] = value }
    suspend fun setRecommendations(value: Boolean) = context.settingsDataStore.edit { it[Keys.recommendations] = value }
    suspend fun setExpirationWarnings(value: Boolean) = context.settingsDataStore.edit { it[Keys.expirationWarnings] = value }
    suspend fun setCardGrid(value: Boolean) = context.settingsDataStore.edit { it[Keys.cardGrid] = value }
    suspend fun setTitleTextSize(value: Float) = context.settingsDataStore.edit { it[Keys.titleTextSize] = value }
    suspend fun setSecondaryTextSize(value: Float) = context.settingsDataStore.edit { it[Keys.secondaryTextSize] = value }
}