package nl.ashhasstudio.kalenda.domain

data class DayGroup(
    val label: String,
    val events: List<CalendarEvent>,
    val hasMore: Boolean = false,
    val moreCount: Int = 0
)
