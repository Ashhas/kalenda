package nl.kabisa.kalenda.data

import kotlinx.coroutines.flow.Flow
import nl.kabisa.kalenda.domain.GoogleAccount
import nl.kabisa.kalenda.domain.WidgetSettings

class IosSettingsRepository : SettingsRepository {
    override fun observeSettings(): Flow<WidgetSettings> = throw NotImplementedError("iOS not implemented")
    override suspend fun getSettings(): WidgetSettings = throw NotImplementedError("iOS not implemented")
    override suspend fun updateSettings(settings: WidgetSettings) = throw NotImplementedError("iOS not implemented")
    override suspend fun addAccount(account: GoogleAccount) = throw NotImplementedError("iOS not implemented")
    override suspend fun removeAccount(accountId: String) = throw NotImplementedError("iOS not implemented")
}
