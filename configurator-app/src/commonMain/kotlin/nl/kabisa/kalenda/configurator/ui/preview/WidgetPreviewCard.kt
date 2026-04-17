package nl.kabisa.kalenda.configurator.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.kabisa.kalenda.domain.CalendarEvent
import nl.kabisa.kalenda.domain.DayGroup
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private val DarkBackground = Color(0xFF1C1C1E)
private val SubtleGray = Color(0xFF9E9E9E)
private val AccentBlue = Color(0xFF4FC3F7)
private val SemiWhite = Color.White.copy(alpha = 0.6f)

@Composable
fun WidgetPreviewCard(dayGroups: List<DayGroup>, deviceTimezone: TimeZone) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBackground, RoundedCornerShape(20.dp))
            .padding(12.dp)
    ) {
        if (dayGroups.isEmpty()) {
            Text("No events", color = SubtleGray, fontSize = 14.sp)
        } else {
            dayGroups.forEachIndexed { index, group ->
                PreviewDayHeader(label = group.label, isToday = index == 0)
                group.events.forEach { event ->
                    PreviewEventRow(event = event, timezone = deviceTimezone)
                }
                if (group.hasMore) {
                    PreviewMoreRow(count = group.moreCount)
                }
            }
        }
    }
}

@Composable
private fun PreviewDayHeader(label: String, isToday: Boolean) {
    if (isToday) {
        val parts = label.split("  ")
        Row(modifier = Modifier.padding(bottom = 4.dp)) {
            Text(parts.getOrElse(0) { label }, color = Color.White, fontSize = 36.sp)
            if (parts.size > 1) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    parts.getOrElse(1) { "" },
                    color = AccentBlue,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 14.dp)
                )
            }
        }
    } else {
        Text(
            label, color = SubtleGray, fontSize = 12.sp,
            modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
        )
    }
}

@Composable
private fun PreviewEventRow(event: CalendarEvent, timezone: TimeZone) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(20.dp)
                .background(Color(event.calendarColor.toULong()))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(event.title, color = Color.White, fontSize = 13.sp, modifier = Modifier.weight(1f))
        val timeLabel = if (event.isAllDay) {
            "All day"
        } else {
            val local = event.startTime.toLocalDateTime(timezone)
            "${local.hour.toString().padStart(2, '0')}:${local.minute.toString().padStart(2, '0')}"
        }
        Text(timeLabel, color = SemiWhite, fontSize = 12.sp)
    }
}

@Composable
private fun PreviewMoreRow(count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(20.dp)
                .background(AccentBlue)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "$count more event${if (count > 1) "s" else ""}...",
            color = AccentBlue,
            fontSize = 13.sp
        )
    }
}
