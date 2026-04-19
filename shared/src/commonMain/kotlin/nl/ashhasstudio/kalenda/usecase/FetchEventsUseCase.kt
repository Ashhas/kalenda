package nl.ashhasstudio.kalenda.usecase

import nl.ashhasstudio.kalenda.data.AuthException
import nl.ashhasstudio.kalenda.data.CalendarRepository
import nl.ashhasstudio.kalenda.data.SettingsRepository
import nl.ashhasstudio.kalenda.data.TokenRefreshOutcome
import nl.ashhasstudio.kalenda.data.TokenRefresher
import nl.ashhasstudio.kalenda.domain.CalendarEvent
import nl.ashhasstudio.kalenda.domain.GoogleAccount

sealed class FetchOutcome {
    data class Ok(val events: List<CalendarEvent>) : FetchOutcome()
    data class NeedsReauth(val accountId: String) : FetchOutcome()
    data class Failed(val accountId: String, val reason: String) : FetchOutcome()
}

class FetchEventsUseCase(
    private val calendarRepository: CalendarRepository,
    private val settingsRepository: SettingsRepository,
    private val tokenRefresher: TokenRefresher? = null
) {
    /**
     * Returns aggregated events and per-account outcomes so the caller can decide whether to
     * overwrite the cache or keep stale data. The cache is updated only when at least one
     * account successfully fetched — so a transient network outage doesn't wipe everything.
     */
    suspend operator fun invoke(): List<FetchOutcome> {
        val settings = settingsRepository.getSettings()
        if (settings.accounts.isEmpty()) return emptyList()

        val outcomes = settings.accounts.map { account ->
            fetchForAccount(account, settings.scrollDays)
        }

        val allEvents = outcomes.flatMap {
            if (it is FetchOutcome.Ok) it.events else emptyList()
        }
        val anySuccess = outcomes.any { it is FetchOutcome.Ok }
        if (anySuccess) {
            // Merge: for accounts that failed, keep the previously-cached events so the widget
            // shows stale data rather than nothing.
            val failedAccountIds = outcomes.mapNotNull {
                when (it) {
                    is FetchOutcome.NeedsReauth -> it.accountId
                    is FetchOutcome.Failed -> it.accountId
                    else -> null
                }
            }.toSet()
            val staleFallback = if (failedAccountIds.isNotEmpty()) {
                calendarRepository.getCachedEvents().events.filter { it.accountId in failedAccountIds }
            } else emptyList()
            calendarRepository.updateCache(allEvents + staleFallback)
        }
        return outcomes
    }

    private suspend fun fetchForAccount(
        account: GoogleAccount,
        days: Int,
    ): FetchOutcome {
        // First sync the calendar list so event filtering has up-to-date calendar metadata.
        // Best-effort: if it fails, we continue with the cached calendar list.
        runCatching { syncCalendarList(account) }

        // Re-read the account from settings so we see the fresh calendars written by syncCalendarList.
        val fresh = settingsRepository.getSettings().accounts.firstOrNull { it.id == account.id } ?: account

        return try {
            val events = calendarRepository.fetchEvents(fresh, days)
            FetchOutcome.Ok(events)
        } catch (e: AuthException) {
            val refreshed = tokenRefresher?.refresh(fresh) ?: return FetchOutcome.NeedsReauth(fresh.id)
            when (refreshed) {
                is TokenRefreshOutcome.Success -> {
                    settingsRepository.updateAccount(refreshed.account)
                    try {
                        FetchOutcome.Ok(calendarRepository.fetchEvents(refreshed.account, days))
                    } catch (e2: Exception) {
                        FetchOutcome.Failed(fresh.id, "retry failed: ${e2.message}")
                    }
                }
                TokenRefreshOutcome.NeedsReauth -> {
                    settingsRepository.setAccountNeedsReauth(fresh.id, true)
                    FetchOutcome.NeedsReauth(fresh.id)
                }
                is TokenRefreshOutcome.Transient -> FetchOutcome.Failed(fresh.id, refreshed.reason)
            }
        } catch (e: Exception) {
            FetchOutcome.Failed(fresh.id, e.message ?: e::class.simpleName ?: "unknown")
        }
    }

    private suspend fun syncCalendarList(account: GoogleAccount) {
        // fetchCalendarList returns calendars merged against `account.calendars`
        // (a snapshot). Re-merge inside mutateSettings against the *current* on-disk
        // account so a concurrent toggle isn't clobbered.
        val remoteCalendars = calendarRepository.fetchCalendarList(account)
        settingsRepository.mutateSettings { current ->
            current.copy(
                accounts = current.accounts.map { acc ->
                    if (acc.id != account.id) acc
                    else {
                        val merged = remoteCalendars.map { remote ->
                            val live = acc.calendars.find { it.id == remote.id }
                            if (live == null) remote
                            else remote.copy(enabled = live.enabled, showAllDay = live.showAllDay)
                        }
                        acc.copy(calendars = merged)
                    }
                }
            )
        }
    }
}
