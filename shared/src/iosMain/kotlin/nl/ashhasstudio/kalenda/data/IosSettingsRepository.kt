package nl.ashhasstudio.kalenda.data

import kotlinx.coroutines.flow.Flow
import nl.ashhasstudio.kalenda.domain.GoogleAccount
import nl.ashhasstudio.kalenda.domain.WidgetSettings

class IosSettingsRepository : SettingsRepository {
    override fun observeSettings(): Flow<WidgetSettings> = throw NotImplementedError("iOS not implemented")
    override suspend fun getSettings(): WidgetSettings = throw NotImplementedError("iOS not implemented")
    override suspend fun updateSettings(settings: WidgetSettings) = throw NotImplementedError("iOS not implemented")
    override suspend fun mutateSettings(transform: (WidgetSettings) -> WidgetSettings) = throw NotImplementedError("iOS not implemented")
    override suspend fun addAccount(account: GoogleAccount) = throw NotImplementedError("iOS not implemented")
    override suspend fun updateAccount(account: GoogleAccount) = throw NotImplementedError("iOS not implemented")
    override suspend fun removeAccount(accountId: String) = throw NotImplementedError("iOS not implemented")
    override suspend fun updateCalendarEnabled(accountId: String, calendarId: String, enabled: Boolean) = throw NotImplementedError("iOS not implemented")
    override suspend fun updateCalendarShowAllDay(accountId: String, calendarId: String, showAllDay: Boolean) = throw NotImplementedError("iOS not implemented")
    override suspend fun setAccountNeedsReauth(accountId: String, needsReauth: Boolean) = throw NotImplementedError("iOS not implemented")
}
