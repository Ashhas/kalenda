package nl.ashhasstudio.kalenda.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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

private val BackgroundColor = ColorProvider(Color(0xFF1C1C1E))
private val MutedWhite = ColorProvider(Color(0x99FFFFFF))

@Composable
fun WidgetContent(
    dayGroups: List<DayGroup>,
    deviceTimezone: TimeZone,
    isScrollable: Boolean
) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .appWidgetBackground()
            .cornerRadius(16.dp)
            .background(BackgroundColor)
    ) {
        if (dayGroups.isEmpty()) {
            EmptyState()
        } else if (isScrollable) {
            ScrollableContent(dayGroups = dayGroups, deviceTimezone = deviceTimezone)
        } else {
            CompactContent(dayGroups = dayGroups, deviceTimezone = deviceTimezone)
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = GlanceModifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No upcoming events",
            style = TextStyle(
                color = MutedWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        )
    }
}

@Composable
private fun ScrollableContent(dayGroups: List<DayGroup>, deviceTimezone: TimeZone) {
    LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
        dayGroups.forEach { dayGroup ->
            item {
                if (dayGroup.label == "Today") {
                    TodayHeader(dayGroup)
                } else {
                    DayHeader(dayGroup)
                }
            }
            items(dayGroup.events) { event ->
                EventRowItem(event = event, deviceTimezone = deviceTimezone)
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
private fun CompactContent(dayGroups: List<DayGroup>, deviceTimezone: TimeZone) {
    Column(modifier = GlanceModifier.fillMaxSize().padding(bottom = 16.dp)) {
        val firstGroup = dayGroups.firstOrNull() ?: return@Column
        if (firstGroup.label == "Today") {
            TodayHeader(firstGroup)
        } else {
            DayHeader(firstGroup)
        }
        firstGroup.events.take(3).forEach { event ->
            EventRowItem(event = event, deviceTimezone = deviceTimezone)
        }
        if (firstGroup.events.size > 3 || firstGroup.hasMore) {
            val remaining = (firstGroup.events.size - 3).coerceAtLeast(0) + firstGroup.moreCount
            if (remaining > 0) {
                OverflowItem(moreCount = remaining)
            }
        }
    }
}
