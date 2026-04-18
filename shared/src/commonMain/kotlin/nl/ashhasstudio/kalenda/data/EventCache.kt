package nl.ashhasstudio.kalenda.data

import nl.ashhasstudio.kalenda.domain.CalendarEvent
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class EventCache(
    val events: List<CalendarEvent> = emptyList(),
    val lastUpdated: Instant? = null
)
