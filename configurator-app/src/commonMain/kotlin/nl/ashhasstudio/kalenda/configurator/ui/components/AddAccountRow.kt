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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalKalendaColors

@Composable
fun AddAccountRow(
    label: String = "Connect Google account",
    onClick: () -> Unit,
) {
    val colors = LocalKalendaColors.current
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(modifier = Modifier.width(3.dp).height(24.dp)) {
            drawLine(
                color = Color(0xFF9E9E9E),
                start = Offset(size.width / 2, 0f),
                end = Offset(size.width / 2, size.height),
                strokeWidth = 2f,
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
            )
        }
        Spacer(modifier = Modifier.width(13.dp))
        Text(
            text = label,
            color = colors.textMuted,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .size(20.dp)
                .border(1.5.dp, colors.textMuted, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(10.dp)) {
                val cx = size.width / 2
                val cy = size.height / 2
                val stroke = 1.5f
                drawLine(Color(0xFF9E9E9E), Offset(cx, 0f), Offset(cx, size.height), stroke, StrokeCap.Round)
                drawLine(Color(0xFF9E9E9E), Offset(0f, cy), Offset(size.width, cy), stroke, StrokeCap.Round)
            }
        }
    }
}
