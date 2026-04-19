package nl.ashhasstudio.kalenda.domain

import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DomainModelsTest {

    @Test
    fun `CalendarEvent isAllDay flag is preserved`() {
        val event = CalendarEvent(
            id = "1",
            title = "All Day Event",
            calendarColor = 0xFFFF6D00L,
            accountId = "acc1",
            isAllDay = true,
            startTime = Instant.parse("2026-04-17T00:00:00Z"),
            endTime = Instant.parse("2026-04-18T00:00:00Z"),
            timezoneId = "Europe/Amsterdam"
        )
        assertTrue(event.isAllDay)
    }

    @Test
    fun `WidgetSettings defaults are sane`() {
        val settings = WidgetSettings()
        assertEquals(7, settings.scrollDays)
        assertEquals(AllDayPosition.TOP, settings.allDayPosition)
        assertTrue(settings.accounts.isEmpty())
        assertEquals(DayMode.ROLLING, settings.dayMode)
        assertEquals(ThemeMode.DARK, settings.themeMode)
    }

    @Test
    fun `DayGroup holds events for a label`() {
        val event = CalendarEvent(
            id = "1", title = "Meeting", calendarColor = 0xFF0000FFL,
            accountId = "acc1", isAllDay = false,
            startTime = Instant.parse("2026-04-17T09:00:00Z"),
            endTime = Instant.parse("2026-04-17T10:00:00Z"),
            timezoneId = "UTC"
        )
        val group = DayGroup(label = "Today", events = listOf(event))
        assertEquals("Today", group.label)
        assertEquals(1, group.events.size)
    }
}
