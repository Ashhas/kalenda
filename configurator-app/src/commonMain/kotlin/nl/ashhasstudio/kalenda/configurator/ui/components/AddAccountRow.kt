package nl.ashhasstudio.kalenda.configurator.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import nl.ashhasstudio.kalenda.configurator.ui.theme.FontSizes
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalKalendaColors
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalStrings
import nl.ashhasstudio.kalenda.configurator.ui.theme.Sizes
import nl.ashhasstudio.kalenda.configurator.ui.theme.Spacing

@Composable
fun AddAccountRow(
    label: String? = null,
    onClick: () -> Unit,
) {
    val colors = LocalKalendaColors.current
    val strings = LocalStrings.current
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.rowHorizontalPadding, vertical = Spacing.rowVerticalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(modifier = Modifier.width(3.dp).height(24.dp)) {
            drawLine(
                color = colors.textSubtle,
                start = Offset(size.width / 2, 0f),
                end = Offset(size.width / 2, size.height),
                strokeWidth = 2f,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
            )
        }
        Spacer(modifier = Modifier.width(13.dp))
        Text(
            text = label ?: strings.calendarsAddGoogle,
            color = colors.textMuted,
            fontSize = FontSizes.body,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(Sizes.smallIcon)
                .border(1.5.dp, colors.textMuted, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(10.dp)) {
                val cx = size.width / 2
                val cy = size.height / 2
                val stroke = 1.5f
                drawLine(colors.textSubtle, Offset(cx, 0f), Offset(cx, size.height), stroke, StrokeCap.Round)
                drawLine(colors.textSubtle, Offset(0f, cy), Offset(size.width, cy), stroke, StrokeCap.Round)
            }
        }
    }
}
