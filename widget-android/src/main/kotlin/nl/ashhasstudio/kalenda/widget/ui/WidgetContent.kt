package nl.ashhasstudio.kalenda.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
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
import nl.ashhasstudio.kalenda.widget.R

private const val TODAY_LABEL = "Today"
private const val COMPACT_EVENTS_LIMIT = 3

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
            .cornerRadius(WidgetShapes.cardRadius)
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
    val context = LocalContext.current
    Box(
        modifier = GlanceModifier.fillMaxSize().padding(WidgetSpacing.cardBottomPadding),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = context.getString(R.string.widget_no_upcoming),
            style = TextStyle(
                color = ColorProvider(theme.textMuted),
                fontSize = WidgetFontSizes.body,
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
                if (dayGroup.label == TODAY_LABEL) TodayHeader(theme)
                else DayHeader(dayGroup, theme)
            }
            if (dayGroup.label == TODAY_LABEL && dayGroup.events.isEmpty()) {
                item { EmptyTodayHint(theme) }
            }
            items(dayGroup.events) { event ->
                EventRowItem(event = event, deviceTimezone = deviceTimezone, theme = theme)
            }
        }
        item {
            Spacer(modifier = GlanceModifier.height(WidgetSpacing.cardBottomPadding))
        }
    }
}

@Composable
private fun CompactContent(
    dayGroups: List<DayGroup>,
    deviceTimezone: TimeZone,
    theme: WidgetTheme,
) {
    Column(modifier = GlanceModifier.fillMaxSize().padding(bottom = WidgetSpacing.cardBottomPadding)) {
        val firstGroup = dayGroups.firstOrNull() ?: return@Column
        if (firstGroup.label == TODAY_LABEL) TodayHeader(theme)
        else DayHeader(firstGroup, theme)

        if (firstGroup.events.isEmpty()) {
            if (firstGroup.label == TODAY_LABEL) EmptyTodayHint(theme)
            val nextGroup = dayGroups.getOrNull(1)
            if (nextGroup != null) {
                DayHeader(nextGroup, theme)
                nextGroup.events.take(COMPACT_EVENTS_LIMIT).forEach { event ->
                    EventRowItem(event = event, deviceTimezone = deviceTimezone, theme = theme)
                }
                val nextRemaining = (nextGroup.events.size - COMPACT_EVENTS_LIMIT).coerceAtLeast(0)
                if (nextRemaining > 0) OverflowItem(moreCount = nextRemaining, theme = theme)
            }
        } else {
            firstGroup.events.take(COMPACT_EVENTS_LIMIT).forEach { event ->
                EventRowItem(event = event, deviceTimezone = deviceTimezone, theme = theme)
            }
            val remaining = (firstGroup.events.size - COMPACT_EVENTS_LIMIT).coerceAtLeast(0)
            if (remaining > 0) OverflowItem(moreCount = remaining, theme = theme)
        }
    }
}

@Composable
fun WidgetErrorContent(theme: WidgetTheme) {
    val context = LocalContext.current
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .appWidgetBackground()
            .cornerRadius(WidgetShapes.cardRadius)
            .background(ColorProvider(theme.background))
            .padding(WidgetSpacing.cardBottomPadding),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = context.getString(R.string.widget_error),
            style = TextStyle(
                color = ColorProvider(theme.textMuted),
                fontSize = WidgetFontSizes.subtle,
                fontWeight = FontWeight.Normal,
            )
        )
    }
}

@Composable
private fun EmptyTodayHint(theme: WidgetTheme) {
    val context = LocalContext.current
    Text(
        text = context.getString(R.string.widget_no_events_today),
        style = TextStyle(
            color = ColorProvider(theme.textMuted),
            fontSize = WidgetFontSizes.small,
            fontWeight = FontWeight.Normal,
        ),
        modifier = GlanceModifier.padding(
            start = WidgetSpacing.cardHorizontalPadding,
            end = WidgetSpacing.cardHorizontalPadding,
            top = WidgetSpacing.eventPillMarginVertical,
            bottom = 6.dp,
        ),
    )
}
