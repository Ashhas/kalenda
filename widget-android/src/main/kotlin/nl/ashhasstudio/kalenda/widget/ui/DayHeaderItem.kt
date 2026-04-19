package nl.ashhasstudio.kalenda.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

private val AccentBlue = ColorProvider(Color(0xFF4FC3F7))

@Composable
fun TodayHeader(dayGroup: DayGroup, theme: WidgetTheme) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val dayNumber = today.dayOfMonth.toString()
    val dayName = today.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(start = 17.dp, end = 17.dp, top = 18.dp, bottom = 8.dp)
    ) {
        Text(
            text = dayNumber,
            style = TextStyle(
                color = ColorProvider(theme.textPrimary),
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = GlanceModifier.width(10.dp))
        Column {
            Spacer(modifier = GlanceModifier.height(16.dp))
            Text(
                text = dayName,
                style = TextStyle(
                    color = AccentBlue,
                    fontSize = 15.sp,
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
            .padding(start = 17.dp, end = 17.dp, top = 14.dp, bottom = 4.dp)
    ) {
        Text(
            text = dayGroup.label,
            style = TextStyle(
                color = ColorProvider(theme.textSubtle),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
