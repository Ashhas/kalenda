package nl.ashhasstudio.kalenda.data

import nl.ashhasstudio.kalenda.domain.CalendarEvent
import nl.ashhasstudio.kalenda.domain.GoogleAccount

class IosCalendarRepository : CalendarRepository {
    override suspend fun fetchEvents(account: GoogleAccount, fromDays: Int): List<CalendarEvent> =
        throw NotImplementedError("iOS not implemented")
    override suspend fun getCachedEvents(): EventCache = throw NotImplementedError("iOS not implemented")
    override suspend fun updateCache(events: List<CalendarEvent>) = throw NotImplementedError("iOS not implemented")
}
