package nl.ashhasstudio.kalenda.domain

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable

@Serializable
data class CalendarEvent(
    val id: String,
    val title: String,
    val calendarColor: Long,
    val accountId: String,
    val calendarId: String = "",
    val isAllDay: Boolean,
    val startTime: Instant,
    val endTime: Instant,
    val timezoneId: String
) {
    // Google (and imported .ics files) can emit TZIDs that aren't in the device's zone db.
    // Falling back to UTC keeps the widget rendering rather than crashing the host process.
    val timezone: TimeZone
        get() = runCatching { TimeZone.of(timezoneId) }.getOrElse { TimeZone.UTC }
}
