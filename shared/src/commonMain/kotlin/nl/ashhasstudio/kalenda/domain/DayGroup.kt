package nl.ashhasstudio.kalenda.domain

data class DayGroup(
    val label: String,
    val events: List<CalendarEvent>,
)
