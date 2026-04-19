package nl.ashhasstudio.kalenda.data

import kotlinx.coroutines.flow.Flow
import nl.ashhasstudio.kalenda.domain.GoogleAccount
import nl.ashhasstudio.kalenda.domain.WidgetSettings

interface SettingsRepository {
    fun observeSettings(): Flow<WidgetSettings>
    suspend fun getSettings(): WidgetSettings

    /**
     * Atomically read-modify-write the settings. `transform` runs while the settings store
     * is locked, so the value it sees is the current value on disk, and the write uses the
     * returned value. Use this instead of `updateSettings(settings.copy(...))` whenever the
     * input is derived from a possibly-stale snapshot.
     */
    suspend fun mutateSettings(transform: (WidgetSettings) -> WidgetSettings)

    suspend fun updateSettings(settings: WidgetSettings)
    suspend fun addAccount(account: GoogleAccount)
    suspend fun updateAccount(account: GoogleAccount)
    suspend fun removeAccount(accountId: String)
    suspend fun updateCalendarEnabled(accountId: String, calendarId: String, enabled: Boolean)
    suspend fun updateCalendarShowAllDay(accountId: String, calendarId: String, showAllDay: Boolean)
    suspend fun setAccountNeedsReauth(accountId: String, needsReauth: Boolean)
}
