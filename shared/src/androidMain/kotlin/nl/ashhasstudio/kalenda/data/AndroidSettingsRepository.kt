package nl.ashhasstudio.kalenda.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.ashhasstudio.kalenda.domain.GoogleAccount
import nl.ashhasstudio.kalenda.domain.WidgetSettings

class AndroidSettingsRepository(context: Context) : SettingsRepository {

    private val appContext = context.applicationContext
    private val dataStore = DataStoreProvider.get(context)
    private val settingsKey = stringPreferencesKey("widget_settings")
    private val json = Json { ignoreUnknownKeys = true }

    private fun hydrateTokens(settings: WidgetSettings): WidgetSettings {
        return settings.copy(
            accounts = settings.accounts.map { account ->
                account.copy(
                    accessToken = SecureTokenStorage.getAccessToken(appContext, account.id),
                    refreshToken = SecureTokenStorage.getRefreshToken(appContext, account.id)
                )
            }
        )
    }

    private fun stripTokens(account: GoogleAccount): GoogleAccount {
        return account.copy(accessToken = "", refreshToken = "")
    }

    override fun observeSettings(): Flow<WidgetSettings> =
        dataStore.data.map { prefs ->
            val raw = prefs[settingsKey]?.let { json.decodeFromString<WidgetSettings>(it) } ?: WidgetSettings()
            hydrateTokens(raw)
        }

    override suspend fun getSettings(): WidgetSettings =
        observeSettings().first()

    override suspend fun updateSettings(settings: WidgetSettings) {
        val stripped = settings.copy(accounts = settings.accounts.map { stripTokens(it) })
        dataStore.edit { prefs ->
            prefs[settingsKey] = json.encodeToString(stripped)
        }
    }

    override suspend fun addAccount(account: GoogleAccount) {
        SecureTokenStorage.saveTokens(appContext, account.id, account.accessToken, account.refreshToken)
        dataStore.edit { prefs ->
            val current = prefs[settingsKey]?.let { json.decodeFromString<WidgetSettings>(it) } ?: WidgetSettings()
            prefs[settingsKey] = json.encodeToString(
                current.copy(accounts = current.accounts + stripTokens(account))
            )
        }
    }

    override suspend fun updateAccount(account: GoogleAccount) {
        SecureTokenStorage.saveTokens(appContext, account.id, account.accessToken, account.refreshToken)
        dataStore.edit { prefs ->
            val current = prefs[settingsKey]?.let { json.decodeFromString<WidgetSettings>(it) } ?: WidgetSettings()
            prefs[settingsKey] = json.encodeToString(
                current.copy(accounts = current.accounts.map {
                    if (it.id == account.id) stripTokens(account) else it
                })
            )
        }
    }

    override suspend fun removeAccount(accountId: String) {
        SecureTokenStorage.removeTokens(appContext, accountId)
        dataStore.edit { prefs ->
            val current = prefs[settingsKey]?.let { json.decodeFromString<WidgetSettings>(it) } ?: WidgetSettings()
            prefs[settingsKey] = json.encodeToString(
                current.copy(accounts = current.accounts.filter { it.id != accountId })
            )
        }
    }

    override suspend fun updateCalendarEnabled(accountId: String, calendarId: String, enabled: Boolean) {
        dataStore.edit { prefs ->
            val current = prefs[settingsKey]?.let { json.decodeFromString<WidgetSettings>(it) } ?: WidgetSettings()
            val updated = current.copy(
                accounts = current.accounts.map { account ->
                    if (account.id == accountId) {
                        account.copy(calendars = account.calendars.map { cal ->
                            if (cal.id == calendarId) cal.copy(enabled = enabled) else cal
                        })
                    } else account
                }
            )
            prefs[settingsKey] = json.encodeToString(updated)
        }
    }

    override suspend fun updateCalendarShowAllDay(accountId: String, calendarId: String, showAllDay: Boolean) {
        dataStore.edit { prefs ->
            val current = prefs[settingsKey]?.let { json.decodeFromString<WidgetSettings>(it) } ?: WidgetSettings()
            val updated = current.copy(
                accounts = current.accounts.map { account ->
                    if (account.id == accountId) {
                        account.copy(calendars = account.calendars.map { cal ->
                            if (cal.id == calendarId) cal.copy(showAllDay = showAllDay) else cal
                        })
                    } else account
                }
            )
            prefs[settingsKey] = json.encodeToString(updated)
        }
    }
}
