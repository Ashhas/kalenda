package nl.ashhasstudio.kalenda.data

import android.content.Context
import android.util.Log
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

    private fun stripTokens(account: GoogleAccount): GoogleAccount =
        account.copy(accessToken = "", refreshToken = "")

    private fun stripTokens(settings: WidgetSettings): WidgetSettings =
        settings.copy(accounts = settings.accounts.map { stripTokens(it) })

    private fun decodeSettings(raw: String?): WidgetSettings? {
        if (raw == null) return null
        return runCatching { json.decodeFromString<WidgetSettings>(raw) }
            .onFailure { e -> Log.w("KalendaSettings", "Corrupt settings JSON", e) }
            .getOrNull()
    }

    override fun observeSettings(): Flow<WidgetSettings> =
        dataStore.data.map { prefs ->
            hydrateTokens(decodeSettings(prefs[settingsKey]) ?: WidgetSettings())
        }

    override suspend fun getSettings(): WidgetSettings =
        observeSettings().first()

    override suspend fun mutateSettings(transform: (WidgetSettings) -> WidgetSettings) {
        dataStore.edit { prefs ->
            val decoded = decodeSettings(prefs[settingsKey])
            // If the row is corrupt (not simply missing), refuse to overwrite — otherwise a
            // one-time decode failure would wipe every account. A missing row (null) is fine;
            // that's just the first-run state.
            val current = decoded ?: if (prefs[settingsKey] == null) WidgetSettings() else return@edit
            // `current` has tokens stripped (as it lives on disk). The transform operates on
            // the stripped form — no risk of tokens ending up in JSON.
            val updated = transform(current)
            prefs[settingsKey] = json.encodeToString(stripTokens(updated))
        }
    }

    /** Deprecated in intent — routes through mutateSettings to avoid races. */
    override suspend fun updateSettings(settings: WidgetSettings) {
        // Accepts a full snapshot; we only persist the non-account fields + account id list
        // so concurrent per-calendar toggles aren't clobbered.
        mutateSettings { current ->
            current.copy(
                scrollDays = settings.scrollDays,
                dayMode = settings.dayMode,
                allDayPosition = settings.allDayPosition,
                accentHue = settings.accentHue,
                themeMode = settings.themeMode,
                // accounts are intentionally left untouched — use addAccount/updateAccount
                // or per-calendar mutators for account mutations.
            )
        }
    }

    override suspend fun addAccount(account: GoogleAccount) {
        SecureTokenStorage.saveTokens(appContext, account.id, account.accessToken, account.refreshToken)
        mutateSettings { current ->
            current.copy(accounts = current.accounts + stripTokens(account))
        }
    }

    override suspend fun updateAccount(account: GoogleAccount) {
        SecureTokenStorage.saveTokens(appContext, account.id, account.accessToken, account.refreshToken)
        mutateSettings { current ->
            current.copy(accounts = current.accounts.map {
                if (it.id == account.id) stripTokens(account) else it
            })
        }
    }

    override suspend fun removeAccount(accountId: String) {
        SecureTokenStorage.removeTokens(appContext, accountId)
        mutateSettings { current ->
            current.copy(accounts = current.accounts.filter { it.id != accountId })
        }
    }

    override suspend fun updateCalendarEnabled(accountId: String, calendarId: String, enabled: Boolean) {
        mutateSettings { current ->
            current.copy(
                accounts = current.accounts.map { account ->
                    if (account.id == accountId) {
                        account.copy(calendars = account.calendars.map { cal ->
                            if (cal.id == calendarId) cal.copy(enabled = enabled) else cal
                        })
                    } else account
                }
            )
        }
    }

    override suspend fun updateCalendarShowAllDay(accountId: String, calendarId: String, showAllDay: Boolean) {
        mutateSettings { current ->
            current.copy(
                accounts = current.accounts.map { account ->
                    if (account.id == accountId) {
                        account.copy(calendars = account.calendars.map { cal ->
                            if (cal.id == calendarId) cal.copy(showAllDay = showAllDay) else cal
                        })
                    } else account
                }
            )
        }
    }

    override suspend fun setAccountNeedsReauth(accountId: String, needsReauth: Boolean) {
        mutateSettings { current ->
            current.copy(
                accounts = current.accounts.map { account ->
                    if (account.id == accountId) account.copy(needsReauth = needsReauth) else account
                }
            )
        }
    }
}
