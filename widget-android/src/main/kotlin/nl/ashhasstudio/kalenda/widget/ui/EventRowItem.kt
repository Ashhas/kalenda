package nl.ashhasstudio.kalenda.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
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
import nl.ashhasstudio.kalenda.widget.R

private const val PILL_BG_ALPHA = 0.13f

@Composable
fun EventRowItem(event: CalendarEvent, deviceTimezone: TimeZone, theme: WidgetTheme) {
    val context = LocalContext.current
    val timeText = if (event.isAllDay) {
        context.getString(R.string.widget_all_day)
    } else {
        val localTime = event.startTime.toLocalDateTime(deviceTimezone).time
        "${localTime.hour.toString().padStart(2, '0')}:${localTime.minute.toString().padStart(2, '0')}"
    }

    val eventColor = Color(event.calendarColor.toInt())
    val pillBg = eventColor.copy(alpha = PILL_BG_ALPHA)

    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(
                horizontal = WidgetSpacing.eventPillMarginHorizontal,
                vertical = WidgetSpacing.eventPillMarginVertical,
            )
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .cornerRadius(WidgetShapes.pillRadius)
                .background(ColorProvider(pillBg))
                .padding(
                    horizontal = WidgetSpacing.eventPillPaddingHorizontal,
                    vertical = WidgetSpacing.eventPillPaddingVertical,
                )
        ) {
            Box(
                modifier = GlanceModifier
                    .width(WidgetSizes.eventBarWidth)
                    .height(WidgetSizes.eventBarHeight)
                    .cornerRadius(WidgetShapes.barRadius)
                    .background(ColorProvider(eventColor))
            ) {}

            Spacer(modifier = GlanceModifier.width(WidgetSpacing.barToTextGap))

            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = event.title,
                    style = TextStyle(
                        color = ColorProvider(theme.textPrimary),
                        fontSize = WidgetFontSizes.body,
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
                    fontSize = WidgetFontSizes.small,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}

@Composable
fun OverflowItem(moreCount: Int, theme: WidgetTheme) {
    val context = LocalContext.current
    val accent = theme.accent
    val pillBg = accent.copy(alpha = PILL_BG_ALPHA)

    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(
                horizontal = WidgetSpacing.eventPillMarginHorizontal,
                vertical = WidgetSpacing.eventPillMarginVertical,
            )
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .cornerRadius(WidgetShapes.pillRadius)
                .background(ColorProvider(pillBg))
                .padding(
                    horizontal = WidgetSpacing.eventPillPaddingHorizontal,
                    vertical = WidgetSpacing.eventPillPaddingVertical,
                )
        ) {
            Box(
                modifier = GlanceModifier
                    .width(WidgetSizes.eventBarWidth)
                    .height(WidgetSizes.eventBarHeight)
                    .cornerRadius(WidgetShapes.barRadius)
                    .background(ColorProvider(accent))
            ) {}

            Spacer(modifier = GlanceModifier.width(WidgetSpacing.barToTextGap))

            val template = if (moreCount > 1) R.string.widget_more_events_many else R.string.widget_more_events_one
            Text(
                text = context.getString(template, moreCount),
                style = TextStyle(
                    color = ColorProvider(accent),
                    fontSize = WidgetFontSizes.subtle,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
