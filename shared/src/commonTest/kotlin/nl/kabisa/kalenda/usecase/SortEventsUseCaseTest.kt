package nl.kabisa.kalenda.usecase

import nl.kabisa.kalenda.domain.AllDayPosition
import nl.kabisa.kalenda.domain.CalendarEvent
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class SortEventsUseCaseTest {

    private val useCase = SortEventsUseCase()

    private fun event(id: String, start: String, allDay: Boolean = false) =
        CalendarEvent(
            id = id, title = "Event $id", calendarColor = 0xFF000000L,
            accountId = "acc1", isAllDay = allDay,
            startTime = Instant.parse(start),
            endTime = Instant.parse(start),
            timezoneId = "UTC"
        )

    @Test
    fun `timed events sorted by start time`() {
        val events = listOf(
            event("late", "2026-04-17T14:00:00Z"),
            event("early", "2026-04-17T08:00:00Z")
        )
        val sorted = useCase(events, AllDayPosition.TOP)
        assertEquals("early", sorted[0].id)
        assertEquals("late", sorted[1].id)
    }

    @Test
    fun `all day events last when AllDayPosition is BOTTOM`() {
        val events = listOf(
            event("timed", "2026-04-17T09:00:00Z", allDay = false),
            event("allday", "2026-04-17T00:00:00Z", allDay = true)
        )
        val sorted = useCase(events, AllDayPosition.BOTTOM)
        assertEquals("timed", sorted[0].id)
        assertEquals("allday", sorted[1].id)
    }
}
