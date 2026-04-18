package nl.ashhasstudio.kalenda.usecase

import nl.ashhasstudio.kalenda.domain.CalendarEvent
import nl.ashhasstudio.kalenda.domain.DayGroup
import nl.ashhasstudio.kalenda.domain.WidgetSettings
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class GroupEventsByDayUseCase {

    private val sortUseCase = SortEventsUseCase()

    operator fun invoke(
        events: List<CalendarEvent>,
        settings: WidgetSettings,
        referenceDate: Instant,
        deviceTimezone: TimeZone
    ): List<DayGroup> {
        val today = referenceDate.toLocalDateTime(deviceTimezone).date
        val tomorrow = today.plus(1, DateTimeUnit.DAY)
        val endDate = today.plus(settings.scrollDays, DateTimeUnit.DAY)

        val allDayHiddenCalendarIds = settings.accounts
            .flatMap { it.calendars }
            .filter { !it.showAllDay }
            .map { it.id }
            .toSet()

        val disabledCalendarIds = settings.accounts
            .flatMap { it.calendars }
            .filter { !it.enabled }
            .map { it.id }
            .toSet()

        val filtered = events.filter { event ->
            if (event.calendarId in disabledCalendarIds) return@filter false
            if (event.isAllDay && event.calendarId in allDayHiddenCalendarIds) return@filter false
            true
        }

        val grouped = mutableMapOf<LocalDate, MutableList<CalendarEvent>>()
        for (event in filtered) {
            if (event.isAllDay) {
                val startDate = event.startTime.toLocalDateTime(event.timezone).date
                val eventEndDate = event.endTime.toLocalDateTime(event.timezone).date
                var date = startDate
                while (date < eventEndDate && date < endDate) {
                    if (date >= today) {
                        grouped.getOrPut(date) { mutableListOf() }.add(event)
                    }
                    date = date.plus(1, DateTimeUnit.DAY)
                }
                if (startDate == eventEndDate && startDate >= today && startDate < endDate) {
                    grouped.getOrPut(startDate) { mutableListOf() }.add(event)
                }
            } else {
                val eventDate = event.startTime.toLocalDateTime(deviceTimezone).date
                if (eventDate >= today && eventDate < endDate) {
                    grouped.getOrPut(eventDate) { mutableListOf() }.add(event)
                }
            }
        }

        return grouped.entries
            .sortedBy { it.key }
            .map { (date, dayEvents) ->
                val sorted = sortUseCase(dayEvents, settings.allDayPosition)
                val label = when (date) {
                    today -> "Today"
                    tomorrow -> "Tomorrow"
                    else -> formatDateLabel(date)
                }
                DayGroup(label = label, events = sorted)
            }
            .filter { it.events.isNotEmpty() }
    }

    private fun formatDateLabel(date: LocalDate): String {
        val monthName = date.month.name.lowercase().replaceFirstChar { it.uppercase() }
        val dayOfWeek = date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
        return "${monthName} ${date.dayOfMonth}  ${dayOfWeek}"
    }
}
