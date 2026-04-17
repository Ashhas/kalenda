package nl.kabisa.kalenda.data

import nl.kabisa.kalenda.domain.CalendarEvent
import nl.kabisa.kalenda.domain.GoogleAccount

interface CalendarRepository {
    suspend fun fetchEvents(account: GoogleAccount, fromDays: Int): List<CalendarEvent>
    suspend fun getCachedEvents(): EventCache
    suspend fun updateCache(events: List<CalendarEvent>)
}
