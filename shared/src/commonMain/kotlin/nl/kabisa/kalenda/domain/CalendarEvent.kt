package nl.kabisa.kalenda.domain

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable

@Serializable
data class CalendarEvent(
    val id: String,
    val title: String,
    val calendarColor: Long,
    val accountId: String,
    val isAllDay: Boolean,
    val startTime: Instant,
    val endTime: Instant,
    val timezoneId: String
) {
    val timezone: TimeZone get() = TimeZone.of(timezoneId)
}
