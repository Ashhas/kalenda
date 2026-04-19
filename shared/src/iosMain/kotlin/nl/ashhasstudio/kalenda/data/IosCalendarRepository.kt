package nl.ashhasstudio.kalenda.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import nl.ashhasstudio.kalenda.domain.CalendarEvent
import nl.ashhasstudio.kalenda.domain.GoogleAccount
import nl.ashhasstudio.kalenda.domain.GoogleCalendar

class IosCalendarRepository : CalendarRepository {
    override suspend fun fetchEvents(account: GoogleAccount, fromDays: Int): List<CalendarEvent> =
        throw NotImplementedError("iOS not implemented")
    override suspend fun fetchCalendarList(account: GoogleAccount): List<GoogleCalendar> =
        throw NotImplementedError("iOS not implemented")
    override suspend fun getCachedEvents(): EventCache = throw NotImplementedError("iOS not implemented")
    override fun observeCachedEvents(): Flow<EventCache> = flowOf(EventCache())
    override suspend fun updateCache(events: List<CalendarEvent>) = throw NotImplementedError("iOS not implemented")
}
