package nl.ashhasstudio.kalenda.configurator.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun NavIcon(name: String, color: Color) {
    Canvas(modifier = Modifier.size(18.dp)) {
        val stroke = Stroke(
            width = 1.6f * (size.width / 20f),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
        val s = size.width / 20f
        when (name) {
            "layout" -> drawLayoutIcon(s, color, stroke)
            "appearance" -> drawAppearanceIcon(s, color, stroke)
            "calendars" -> drawCalendarsIcon(s, color, stroke)
        }
    }
}

private fun DrawScope.drawLayoutIcon(s: Float, color: Color, stroke: Stroke) {
    drawRoundRect(
        color = color,
        topLeft = Offset(3f * s, 3.5f * s),
        size = Size(14f * s, 13f * s),
        cornerRadius = CornerRadius(2f * s),
        style = stroke
    )
    drawLine(color, Offset(3f * s, 8f * s), Offset(17f * s, 8f * s), stroke.width)
    drawLine(color, Offset(8f * s, 8f * s), Offset(8f * s, 16.5f * s), stroke.width)
}

private fun DrawScope.drawAppearanceIcon(s: Float, color: Color, stroke: Stroke) {
    val path = Path().apply {
        moveTo(10f * s, 3f * s)
        cubicTo(10f * s, 3f * s, 4.5f * s, 9f * s, 4.5f * s, 12.5f * s)
        cubicTo(4.5f * s, 15.5f * s, 7f * s, 17.5f * s, 10f * s, 17.5f * s)
        cubicTo(13f * s, 17.5f * s, 15.5f * s, 15.5f * s, 15.5f * s, 12.5f * s)
        cubicTo(15.5f * s, 9f * s, 10f * s, 3f * s, 10f * s, 3f * s)
        close()
    }
    drawPath(path, color, style = stroke)
}

private fun DrawScope.drawCalendarsIcon(s: Float, color: Color, stroke: Stroke) {
    drawRoundRect(
        color = color,
        topLeft = Offset(3f * s, 4.5f * s),
        size = Size(14f * s, 12f * s),
        cornerRadius = CornerRadius(2f * s),
        style = stroke
    )
    drawLine(color, Offset(3f * s, 8.5f * s), Offset(17f * s, 8.5f * s), stroke.width)
    drawLine(color, Offset(7f * s, 3f * s), Offset(7f * s, 6f * s), stroke.width, StrokeCap.Round)
    drawLine(color, Offset(13f * s, 3f * s), Offset(13f * s, 6f * s), stroke.width, StrokeCap.Round)
}
