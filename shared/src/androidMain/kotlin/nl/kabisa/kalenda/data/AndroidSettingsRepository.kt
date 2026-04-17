package nl.kabisa.kalenda.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.kabisa.kalenda.domain.GoogleAccount
import nl.kabisa.kalenda.domain.WidgetSettings

class AndroidSettingsRepository(context: Context) : SettingsRepository {

    private val dataStore = DataStoreProvider.get(context)
    private val settingsKey = stringPreferencesKey("widget_settings")

    override fun observeSettings(): Flow<WidgetSettings> =
        dataStore.data.map { prefs ->
            prefs[settingsKey]?.let { Json.decodeFromString(it) } ?: WidgetSettings()
        }

    override suspend fun getSettings(): WidgetSettings =
        observeSettings().first()

    override suspend fun updateSettings(settings: WidgetSettings) {
        dataStore.edit { prefs ->
            prefs[settingsKey] = Json.encodeToString(settings)
        }
    }

    override suspend fun addAccount(account: GoogleAccount) {
        val current = getSettings()
        val updated = current.copy(accounts = current.accounts + account)
        updateSettings(updated)
    }

    override suspend fun removeAccount(accountId: String) {
        val current = getSettings()
        val updated = current.copy(accounts = current.accounts.filter { it.id != accountId })
        updateSettings(updated)
    }
}
