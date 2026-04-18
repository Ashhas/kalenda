package nl.ashhasstudio.kalenda.data

import kotlinx.coroutines.flow.Flow
import nl.ashhasstudio.kalenda.domain.GoogleAccount
import nl.ashhasstudio.kalenda.domain.WidgetSettings

interface SettingsRepository {
    fun observeSettings(): Flow<WidgetSettings>
    suspend fun getSettings(): WidgetSettings
    suspend fun updateSettings(settings: WidgetSettings)
    suspend fun addAccount(account: GoogleAccount)
    suspend fun updateAccount(account: GoogleAccount)
    suspend fun removeAccount(accountId: String)
    suspend fun updateCalendarEnabled(accountId: String, calendarId: String, enabled: Boolean)
    suspend fun updateCalendarShowAllDay(accountId: String, calendarId: String, showAllDay: Boolean)
}
