package nl.kabisa.kalenda.usecase

import nl.kabisa.kalenda.domain.AllDayPosition
import nl.kabisa.kalenda.domain.CalendarEvent

class SortEventsUseCase {
    operator fun invoke(events: List<CalendarEvent>, allDayPosition: AllDayPosition): List<CalendarEvent> {
        val (allDay, timed) = events.partition { it.isAllDay }
        val sortedTimed = timed.sortedBy { it.startTime }
        return when (allDayPosition) {
            AllDayPosition.TOP -> allDay + sortedTimed
            AllDayPosition.BOTTOM -> sortedTimed + allDay
            AllDayPosition.HIDDEN -> sortedTimed
        }
    }
}
