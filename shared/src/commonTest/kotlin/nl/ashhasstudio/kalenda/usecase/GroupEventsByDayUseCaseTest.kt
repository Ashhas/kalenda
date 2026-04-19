package nl.ashhasstudio.kalenda.usecase

import nl.ashhasstudio.kalenda.domain.AllDayPosition
import nl.ashhasstudio.kalenda.domain.CalendarEvent
import nl.ashhasstudio.kalenda.domain.DayMode
import nl.ashhasstudio.kalenda.domain.WidgetSettings
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

class GroupEventsByDayUseCaseTest {

    private val tz = TimeZone.of("Europe/Amsterdam")
    private val useCase = GroupEventsByDayUseCase()

    private fun event(id: String, start: String, end: String, allDay: Boolean = false, tz: String = "Europe/Amsterdam") =
        CalendarEvent(
            id = id, title = "Event $id", calendarColor = 0xFF000000L,
            accountId = "acc1", isAllDay = allDay,
            startTime = Instant.parse(start), endTime = Instant.parse(end),
            timezoneId = tz
        )

    @Test
    fun `groups events into correct day buckets`() {
        val events = listOf(
            event("1", "2026-04-17T09:00:00Z", "2026-04-17T10:00:00Z"),
            event("2", "2026-04-18T14:00:00Z", "2026-04-18T15:00:00Z")
        )
        val settings = WidgetSettings(scrollDays = 7)
        val groups = useCase(events, settings, referenceDate = Instant.parse("2026-04-17T00:00:00Z"), deviceTimezone = tz)
        assertEquals(2, groups.size)
        assertEquals("Today", groups[0].label)
        assertEquals("Tomorrow", groups[1].label)
    }

    @Test
    fun `all day events respect AllDayPosition HIDDEN`() {
        val events = listOf(
            event("1", "2026-04-17T00:00:00Z", "2026-04-18T00:00:00Z", allDay = true),
            event("2", "2026-04-17T09:00:00Z", "2026-04-17T10:00:00Z", allDay = false)
        )
        val settings = WidgetSettings(allDayPosition = AllDayPosition.HIDDEN)
        val groups = useCase(events, settings, referenceDate = Instant.parse("2026-04-17T00:00:00Z"), deviceTimezone = tz)
        assertEquals(1, groups[0].events.size)
        assertEquals("2", groups[0].events[0].id)
    }

    @Test
    fun `all day events appear first when AllDayPosition is TOP`() {
        val events = listOf(
            event("timed", "2026-04-17T09:00:00Z", "2026-04-17T10:00:00Z", allDay = false),
            event("allday", "2026-04-17T00:00:00Z", "2026-04-18T00:00:00Z", allDay = true)
        )
        val settings = WidgetSettings(allDayPosition = AllDayPosition.TOP)
        val groups = useCase(events, settings, referenceDate = Instant.parse("2026-04-17T00:00:00Z"), deviceTimezone = tz)
        assertEquals("allday", groups[0].events[0].id)
        assertEquals("timed", groups[0].events[1].id)
    }

    @Test
    fun `all day events bucketed correctly in negative UTC offset timezone`() {
        val nyTz = TimeZone.of("America/New_York")
        val events = listOf(
            event("allday", "2026-04-18T00:00:00Z", "2026-04-19T00:00:00Z", allDay = true, tz = "UTC")
        )
        val settings = WidgetSettings(scrollDays = 7)
        val groups = useCase(events, settings, referenceDate = Instant.parse("2026-04-18T04:00:00Z"), deviceTimezone = nyTz)
        assertEquals(1, groups.size)
        assertEquals("Today", groups[0].label)
    }

    @Test
    fun `THIS_WEEK mode on Monday includes seven days`() {
        assertEquals(7, daysLeftInWeekInclusive(LocalDate(2026, 4, 13))) // Mon
    }

    @Test
    fun `THIS_WEEK mode on Friday includes three days`() {
        assertEquals(3, daysLeftInWeekInclusive(LocalDate(2026, 4, 17))) // Fri
    }

    @Test
    fun `THIS_WEEK mode on Sunday includes one day`() {
        assertEquals(1, daysLeftInWeekInclusive(LocalDate(2026, 4, 19))) // Sun
    }

    @Test
    fun `THIS_WEEK honors day count in grouping`() {
        // 2026-04-17 is Friday; should cap at Fri/Sat/Sun and exclude next Mon.
        val events = listOf(
            event("fri", "2026-04-17T09:00:00Z", "2026-04-17T10:00:00Z"),
            event("mon", "2026-04-20T09:00:00Z", "2026-04-20T10:00:00Z")
        )
        val settings = WidgetSettings(dayMode = DayMode.THIS_WEEK, scrollDays = 14)
        val groups = useCase(events, settings, referenceDate = Instant.parse("2026-04-17T06:00:00Z"), deviceTimezone = tz)
        assertEquals(1, groups.size)
        assertEquals("Today", groups[0].label)
        assertEquals(1, groups[0].events.size)
    }
}
