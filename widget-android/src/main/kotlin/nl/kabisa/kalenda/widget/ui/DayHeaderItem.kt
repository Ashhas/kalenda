package nl.kabisa.kalenda.widget.ui

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
import nl.kabisa.kalenda.domain.DayGroup
import java.time.LocalDate
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale

private val AccentBlue = ColorProvider(Color(0xFF4FC3F7))
private val MutedGrey = ColorProvider(Color(0xFF9E9E9E))
private val White = ColorProvider(Color.White)

@Composable
fun TodayHeader(dayGroup: DayGroup) {
    val today = LocalDate.now()
    val dayNumber = today.dayOfMonth.toString()
    val dayName = today.dayOfWeek.getDisplayName(JavaTextStyle.FULL, Locale.getDefault())

    Column(
        modifier = GlanceModifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            Text(
                text = dayNumber,
                style = TextStyle(
                    color = White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = GlanceModifier.width(8.dp))
            Text(
                text = dayName,
                style = TextStyle(
                    color = AccentBlue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun DayHeader(dayGroup: DayGroup) {
    Column(
        modifier = GlanceModifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Spacer(modifier = GlanceModifier.height(4.dp))
        Text(
            text = dayGroup.label,
            style = TextStyle(
                color = MutedGrey,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
