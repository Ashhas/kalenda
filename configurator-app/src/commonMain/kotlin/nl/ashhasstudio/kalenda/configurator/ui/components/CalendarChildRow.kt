package nl.ashhasstudio.kalenda.configurator.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.ashhasstudio.kalenda.configurator.ui.theme.FontSizes
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalKalendaColors
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalStrings

@Composable
fun CalendarChildRow(
    color: Color,
    name: String,
    enabled: Boolean,
    showAllDay: Boolean,
    onToggle: () -> Unit,
    onToggleAllDay: () -> Unit,
    parentEnabled: Boolean,
) {
    val colors = LocalKalendaColors.current
    val strings = LocalStrings.current
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(enabled = parentEnabled) { onToggle() }
            .heightIn(min = 44.dp)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(2.dp))
                .then(
                    if (enabled) Modifier.background(color)
                    else Modifier.border(1.5.dp, colors.textSubtle, RoundedCornerShape(2.dp))
                )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = name,
                color = if (enabled) colors.textPrimary else colors.textMuted,
                fontSize = FontSizes.subtle,
                lineHeight = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (enabled && !showAllDay) {
                Text(
                    text = strings.calendarsAllDayHidden,
                    color = colors.textSubtle,
                    fontSize = FontSizes.tiny,
                    lineHeight = 14.sp,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .size(28.dp, 24.dp)
                .clip(RoundedCornerShape(6.dp))
                .then(
                    if (!showAllDay) Modifier.border(1.dp, colors.divider, RoundedCornerShape(6.dp))
                    else Modifier
                )
                .clickable(enabled = parentEnabled && enabled) { onToggleAllDay() }
                .padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            SunIcon(
                color = if (showAllDay) color else colors.textSubtle,
                strikethrough = !showAllDay,
            )
        }
    }
}

@Composable
private fun SunIcon(color: Color, strikethrough: Boolean) {
    Canvas(modifier = Modifier.size(14.dp)) {
        val cx = size.width / 2
        val cy = size.height / 2
        val r = size.width * 0.17f
        val rayLen = size.width * 0.11f
        val rayDist = size.width * 0.3f
        val stroke = Stroke(width = size.width * 0.1f, cap = StrokeCap.Round)

        drawCircle(color, r, style = stroke)

        val rayAngles = if (strikethrough) listOf(0f, 90f, 180f, 270f)
        else listOf(0f, 45f, 90f, 135f, 180f, 225f, 270f, 315f)
        rayAngles.forEach { angle ->
            val rad = (angle.toDouble() * kotlin.math.PI) / 180.0
            val cos = kotlin.math.cos(rad).toFloat()
            val sin = kotlin.math.sin(rad).toFloat()
            drawLine(
                color,
                Offset(cx + cos * rayDist, cy - sin * rayDist),
                Offset(cx + cos * (rayDist + rayLen), cy - sin * (rayDist + rayLen)),
                stroke.width, StrokeCap.Round,
            )
        }

        if (strikethrough) {
            drawLine(
                color,
                Offset(size.width * 0.14f, size.height * 0.86f),
                Offset(size.width * 0.86f, size.height * 0.14f),
                stroke.width, StrokeCap.Round,
            )
        }
    }
}
