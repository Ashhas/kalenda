package nl.kabisa.kalenda.data

import nl.kabisa.kalenda.domain.CalendarEvent
import nl.kabisa.kalenda.domain.GoogleAccount

class IosCalendarRepository : CalendarRepository {
    override suspend fun fetchEvents(account: GoogleAccount, fromDays: Int): List<CalendarEvent> =
        throw NotImplementedError("iOS not implemented")
    override suspend fun getCachedEvents(): EventCache = throw NotImplementedError("iOS not implemented")
    override suspend fun updateCache(events: List<CalendarEvent>) = throw NotImplementedError("iOS not implemented")
}
