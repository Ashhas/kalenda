package nl.kabisa.kalenda.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import nl.kabisa.kalenda.domain.CalendarEvent

private val MutedWhite = ColorProvider(Color(0x99FFFFFF))
private val EventWhite = ColorProvider(Color.White)
private val OverflowBlue = ColorProvider(Color(0xFF4FC3F7))

@Composable
fun EventRowItem(event: CalendarEvent, deviceTimezone: TimeZone) {
    val timeText = if (event.isAllDay) {
        "All day"
    } else {
        val localTime = event.startTime.toLocalDateTime(deviceTimezone).time
        String.format("%02d:%02d", localTime.hour, localTime.minute)
    }

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 3.dp)
    ) {
        // Colored left bar
        Box(
            modifier = GlanceModifier
                .width(3.dp)
                .height(32.dp)
                .background(ColorProvider(Color(event.calendarColor.toInt())))
        ) {}

        Spacer(modifier = GlanceModifier.width(8.dp))

        // Title
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = event.title,
                style = TextStyle(
                    color = EventWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                ),
                maxLines = 1
            )
        }

        Spacer(modifier = GlanceModifier.width(8.dp))

        // Time
        Text(
            text = timeText,
            style = TextStyle(
                color = MutedWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal
            )
        )
    }
}

@Composable
fun OverflowItem(moreCount: Int) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 3.dp)
    ) {
        // Blue bar for overflow
        Box(
            modifier = GlanceModifier
                .width(3.dp)
                .height(24.dp)
                .background(OverflowBlue)
        ) {}

        Spacer(modifier = GlanceModifier.width(8.dp))

        Text(
            text = "$moreCount more events...",
            style = TextStyle(
                color = OverflowBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
