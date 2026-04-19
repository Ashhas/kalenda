package nl.ashhasstudio.kalenda.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.datetime.TimeZone
import nl.ashhasstudio.kalenda.domain.DayGroup

@Composable
fun WidgetContent(
    dayGroups: List<DayGroup>,
    deviceTimezone: TimeZone,
    isScrollable: Boolean,
    theme: WidgetTheme,
) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .appWidgetBackground()
            .cornerRadius(16.dp)
            .background(ColorProvider(theme.background))
    ) {
        if (dayGroups.isEmpty()) {
            EmptyState(theme)
        } else if (isScrollable) {
            ScrollableContent(dayGroups, deviceTimezone, theme)
        } else {
            CompactContent(dayGroups, deviceTimezone, theme)
        }
    }
}

@Composable
private fun EmptyState(theme: WidgetTheme) {
    Box(
        modifier = GlanceModifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No upcoming events",
            style = TextStyle(
                color = ColorProvider(theme.textMuted),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        )
    }
}

@Composable
private fun ScrollableContent(
    dayGroups: List<DayGroup>,
    deviceTimezone: TimeZone,
    theme: WidgetTheme,
) {
    LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
        dayGroups.forEach { dayGroup ->
            item {
                if (dayGroup.label == "Today") {
                    TodayHeader(dayGroup, theme)
                } else {
                    DayHeader(dayGroup, theme)
                }
            }
            if (dayGroup.label == "Today" && dayGroup.events.isEmpty()) {
                item { EmptyTodayHint(theme) }
            }
            items(dayGroup.events) { event ->
                EventRowItem(event = event, deviceTimezone = deviceTimezone, theme = theme)
            }
            if (dayGroup.hasMore) {
                item {
                    OverflowItem(moreCount = dayGroup.moreCount)
                }
            }
        }
        item {
            Spacer(modifier = GlanceModifier.height(16.dp))
        }
    }
}

@Composable
private fun CompactContent(
    dayGroups: List<DayGroup>,
    deviceTimezone: TimeZone,
    theme: WidgetTheme,
) {
    Column(modifier = GlanceModifier.fillMaxSize().padding(bottom = 16.dp)) {
        val firstGroup = dayGroups.firstOrNull() ?: return@Column
        if (firstGroup.label == "Today") {
            TodayHeader(firstGroup, theme)
        } else {
            DayHeader(firstGroup, theme)
        }
        if (firstGroup.events.isEmpty()) {
            if (firstGroup.label == "Today") EmptyTodayHint(theme)
            val nextGroup = dayGroups.getOrNull(1)
            if (nextGroup != null) {
                DayHeader(nextGroup, theme)
                nextGroup.events.take(3).forEach { event ->
                    EventRowItem(event = event, deviceTimezone = deviceTimezone, theme = theme)
                }
                if (nextGroup.events.size > 3 || nextGroup.hasMore) {
                    val remaining = (nextGroup.events.size - 3).coerceAtLeast(0) + nextGroup.moreCount
                    if (remaining > 0) OverflowItem(moreCount = remaining)
                }
            }
        } else {
            firstGroup.events.take(3).forEach { event ->
                EventRowItem(event = event, deviceTimezone = deviceTimezone, theme = theme)
            }
            if (firstGroup.events.size > 3 || firstGroup.hasMore) {
                val remaining = (firstGroup.events.size - 3).coerceAtLeast(0) + firstGroup.moreCount
                if (remaining > 0) OverflowItem(moreCount = remaining)
            }
        }
    }
}

@Composable
private fun EmptyTodayHint(theme: WidgetTheme) {
    Text(
        text = "No events today",
        style = TextStyle(
            color = ColorProvider(theme.textMuted),
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
        ),
        modifier = GlanceModifier.padding(start = 17.dp, end = 17.dp, top = 2.dp, bottom = 6.dp),
    )
}
