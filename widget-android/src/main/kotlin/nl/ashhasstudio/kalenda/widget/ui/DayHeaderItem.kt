package nl.ashhasstudio.kalenda.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
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
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import nl.ashhasstudio.kalenda.domain.DayGroup

@Composable
fun TodayHeader(theme: WidgetTheme) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val dayNumber = today.dayOfMonth.toString()
    val dayName = today.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(
                start = WidgetSpacing.cardHorizontalPadding,
                end = WidgetSpacing.cardHorizontalPadding,
                top = 18.dp,
                bottom = 8.dp,
            )
    ) {
        Text(
            text = dayNumber,
            style = TextStyle(
                color = ColorProvider(theme.textPrimary),
                fontSize = WidgetFontSizes.heroNumber,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = GlanceModifier.width(WidgetSizes.heroNumberToLabelSpacing))
        Column {
            Spacer(modifier = GlanceModifier.height(WidgetSizes.heroLabelVerticalOffset))
            Text(
                text = dayName,
                style = TextStyle(
                    color = ColorProvider(theme.accent),
                    fontSize = WidgetFontSizes.heroLabel,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun DayHeader(dayGroup: DayGroup, theme: WidgetTheme) {
    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(
                start = WidgetSpacing.cardHorizontalPadding,
                end = WidgetSpacing.cardHorizontalPadding,
                top = 14.dp,
                bottom = 4.dp,
            )
    ) {
        Text(
            text = dayGroup.label,
            style = TextStyle(
                color = ColorProvider(theme.textSubtle),
                fontSize = WidgetFontSizes.small,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
