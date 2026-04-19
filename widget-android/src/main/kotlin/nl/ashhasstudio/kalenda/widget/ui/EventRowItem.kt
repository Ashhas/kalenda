package nl.ashhasstudio.kalenda.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.cornerRadius
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
import nl.ashhasstudio.kalenda.domain.CalendarEvent

private val OverflowBlue = ColorProvider(Color(0xFF4FC3F7))

@Composable
fun EventRowItem(event: CalendarEvent, deviceTimezone: TimeZone, theme: WidgetTheme) {
    val timeText = if (event.isAllDay) {
        "All day"
    } else {
        val localTime = event.startTime.toLocalDateTime(deviceTimezone).time
        String.format("%02d:%02d", localTime.hour, localTime.minute)
    }

    val eventColor = Color(event.calendarColor.toInt())
    val pillBg = eventColor.copy(alpha = 0.13f)

    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .cornerRadius(4.dp)
                .background(ColorProvider(pillBg))
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = GlanceModifier
                    .width(3.dp)
                    .height(16.dp)
                    .cornerRadius(2.dp)
                    .background(ColorProvider(eventColor))
            ) {}

            Spacer(modifier = GlanceModifier.width(10.dp))

            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = event.title,
                    style = TextStyle(
                        color = ColorProvider(theme.textPrimary),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    maxLines = 1
                )
            }

            Spacer(modifier = GlanceModifier.width(8.dp))

            Text(
                text = timeText,
                style = TextStyle(
                    color = ColorProvider(theme.textPrimary),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}

@Composable
fun OverflowItem(moreCount: Int) {
    val pillBg = Color(0xFF4FC3F7).copy(alpha = 0.13f)

    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .cornerRadius(4.dp)
                .background(ColorProvider(pillBg))
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = GlanceModifier
                    .width(3.dp)
                    .height(16.dp)
                    .cornerRadius(2.dp)
                    .background(OverflowBlue)
            ) {}

            Spacer(modifier = GlanceModifier.width(10.dp))

            Text(
                text = "$moreCount more event${if (moreCount > 1) "s" else ""}...",
                style = TextStyle(
                    color = OverflowBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
