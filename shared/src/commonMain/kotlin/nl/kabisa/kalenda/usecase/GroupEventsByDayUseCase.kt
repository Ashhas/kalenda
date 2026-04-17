package nl.kabisa.kalenda.usecase

import nl.kabisa.kalenda.domain.AllDayPosition
import nl.kabisa.kalenda.domain.CalendarEvent
import nl.kabisa.kalenda.domain.DayGroup
import nl.kabisa.kalenda.domain.WidgetSettings
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

        val grouped = mutableMapOf<LocalDate, MutableList<CalendarEvent>>()
        for (event in events) {
            val eventDate = event.startTime.toLocalDateTime(deviceTimezone).date
            if (eventDate >= today && eventDate < endDate) {
                grouped.getOrPut(eventDate) { mutableListOf() }.add(event)
            }
        }

        return grouped.entries
            .sortedBy { it.key }
            .map { (date, dayEvents) ->
                val filtered = if (settings.allDayPosition == AllDayPosition.HIDDEN) {
                    dayEvents.filter { !it.isAllDay }
                } else {
                    dayEvents
                }
                val sorted = sortUseCase(filtered, settings.allDayPosition)
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
