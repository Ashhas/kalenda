package nl.kabisa.kalenda.data

import kotlinx.coroutines.flow.Flow
import nl.kabisa.kalenda.domain.GoogleAccount
import nl.kabisa.kalenda.domain.WidgetSettings

interface SettingsRepository {
    fun observeSettings(): Flow<WidgetSettings>
    suspend fun getSettings(): WidgetSettings
    suspend fun updateSettings(settings: WidgetSettings)
    suspend fun addAccount(account: GoogleAccount)
    suspend fun removeAccount(accountId: String)
}
