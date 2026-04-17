package nl.kabisa.kalenda.data

import nl.kabisa.kalenda.domain.CalendarEvent
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class EventCache(
    val events: List<CalendarEvent> = emptyList(),
    val lastUpdated: Instant? = null
)
