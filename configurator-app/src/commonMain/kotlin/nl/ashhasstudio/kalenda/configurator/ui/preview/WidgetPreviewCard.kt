package nl.ashhasstudio.kalenda.configurator.ui.preview

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import nl.ashhasstudio.kalenda.configurator.ui.theme.AccentBlue
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalKalendaColors
import nl.ashhasstudio.kalenda.domain.CalendarEvent
import nl.ashhasstudio.kalenda.domain.DayGroup

@Composable
fun WidgetPreviewCard(dayGroups: List<DayGroup>, deviceTimezone: TimeZone) {
    val colors = LocalKalendaColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBackground)
            .padding(bottom = 16.dp)
    ) {
        if (dayGroups.isEmpty()) {
            Text(
                "No events",
                color = colors.textSubtle,
                fontSize = 14.sp,
                modifier = Modifier.padding(18.dp)
            )
        } else {
            dayGroups.forEachIndexed { index, group ->
                if (index == 0) {
                    PreviewDayHeader(isToday = true)
                } else {
                    Text(
                        group.label,
                        color = colors.textSubtle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 17.dp, top = 14.dp, bottom = 4.dp)
                    )
                }
                if (index == 0 && group.events.isEmpty()) {
                    Text(
                        "No events today",
                        color = colors.textSubtle,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 17.dp, top = 2.dp, bottom = 6.dp)
                    )
                }
                group.events.forEach { event ->
                    PillEventRow(event = event, timezone = deviceTimezone)
                }
                if (group.hasMore) {
                    PillMoreRow(count = group.moreCount)
                }
            }
        }
    }
}

@Composable
private fun PreviewDayHeader(isToday: Boolean) {
    if (!isToday) return
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val dayNumber = today.dayOfMonth.toString()
    val dayName = today.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
    Row(
        modifier = Modifier.padding(start = 17.dp, top = 18.dp, bottom = 8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            dayNumber,
            color = Color.White,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 36.sp,
            letterSpacing = (-0.5).sp,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            dayName,
            color = AccentBlue,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }
}

@Composable
private fun PillEventRow(event: CalendarEvent, timezone: TimeZone) {
    val eventColor = Color(event.calendarColor.toInt())
    val pillBg = eventColor.copy(alpha = 0.13f)
    Row(
        modifier = Modifier
            .padding(horizontal = 18.dp, vertical = 2.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(pillBg)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(1.5.dp))
                .background(eventColor)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            event.title,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )
        val timeLabel = if (event.isAllDay) {
            "All day"
        } else {
            val local = event.startTime.toLocalDateTime(timezone)
            "${local.hour.toString().padStart(2, '0')}:${local.minute.toString().padStart(2, '0')}"
        }
        Text(
            timeLabel,
            color = Color.White,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun PillMoreRow(count: Int) {
    val pillBg = AccentBlue.copy(alpha = 0.13f)
    Row(
        modifier = Modifier
            .padding(horizontal = 18.dp, vertical = 2.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(pillBg)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(1.5.dp))
                .background(AccentBlue)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            "$count more event${if (count > 1) "s" else ""}...",
            color = AccentBlue,
            fontSize = 13.sp,
        )
    }
}
