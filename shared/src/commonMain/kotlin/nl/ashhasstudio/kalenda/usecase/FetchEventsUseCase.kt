package nl.ashhasstudio.kalenda.usecase

import nl.ashhasstudio.kalenda.data.AuthException
import nl.ashhasstudio.kalenda.data.CalendarRepository
import nl.ashhasstudio.kalenda.data.SettingsRepository
import nl.ashhasstudio.kalenda.data.TokenRefresher
import nl.ashhasstudio.kalenda.domain.CalendarEvent
import nl.ashhasstudio.kalenda.domain.GoogleAccount

class FetchEventsUseCase(
    private val calendarRepository: CalendarRepository,
    private val settingsRepository: SettingsRepository,
    private val tokenRefresher: TokenRefresher? = null
) {
    suspend operator fun invoke(): List<CalendarEvent> {
        val settings = settingsRepository.getSettings()
        val allEvents = mutableListOf<CalendarEvent>()

        for (account in settings.accounts) {
            val (events, updatedAccount) = fetchWithRetry(account, settings.scrollDays)
            if (updatedAccount != null) {
                settingsRepository.updateAccount(updatedAccount)
            }
            allEvents.addAll(events)
        }

        calendarRepository.updateCache(allEvents)
        return allEvents
    }

    private suspend fun fetchWithRetry(
        account: GoogleAccount,
        days: Int
    ): Pair<List<CalendarEvent>, GoogleAccount?> {
        return try {
            syncCalendarList(account)
            val events = calendarRepository.fetchEvents(account, days)
            events to null
        } catch (e: Exception) {
            if (isAuthError(e) && tokenRefresher != null) {
                val refreshed = tokenRefresher.refreshAccount(account)
                    ?: return emptyList<CalendarEvent>() to null
                settingsRepository.updateAccount(refreshed)
                try {
                    syncCalendarList(refreshed)
                    val events = calendarRepository.fetchEvents(refreshed, days)
                    events to null
                } catch (_: Exception) {
                    emptyList<CalendarEvent>() to null
                }
            } else {
                emptyList<CalendarEvent>() to null
            }
        }
    }

    private suspend fun syncCalendarList(account: GoogleAccount) {
        try {
            val remoteCalendars = calendarRepository.fetchCalendarList(account)
            val updated = account.copy(calendars = remoteCalendars)
            settingsRepository.updateAccount(updated)
        } catch (_: Exception) {
            // Calendar list sync is best-effort; events still fetch
        }
    }

    private fun isAuthError(e: Exception): Boolean {
        if (e is AuthException) return true
        val message = e.message ?: return false
        return "401" in message || "Unauthorized" in message || "403" in message
    }
}
