package nl.ashhasstudio.kalenda.data

import nl.ashhasstudio.kalenda.domain.CalendarEvent
import nl.ashhasstudio.kalenda.domain.GoogleAccount
import nl.ashhasstudio.kalenda.domain.GoogleCalendar

interface CalendarRepository {
    suspend fun fetchEvents(account: GoogleAccount, fromDays: Int): List<CalendarEvent>
    suspend fun fetchCalendarList(account: GoogleAccount): List<GoogleCalendar>
    suspend fun getCachedEvents(): EventCache
    suspend fun updateCache(events: List<CalendarEvent>)
}
