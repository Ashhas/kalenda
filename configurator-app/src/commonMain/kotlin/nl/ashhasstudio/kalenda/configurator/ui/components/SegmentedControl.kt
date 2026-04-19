package nl.ashhasstudio.kalenda.configurator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalKalendaColors

data class SegmentOption<T>(val value: T, val label: String)

@Composable
fun <T> SegmentedControl(
    options: List<SegmentOption<T>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalKalendaColors.current
    Row(
        modifier = modifier
            .padding(horizontal = 17.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.rowHover)
            .padding(3.dp)
    ) {
        options.forEach { opt ->
            val active = opt.value == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (active) colors.rowPress else Color.Transparent)
                    .clickable { onSelect(opt.value) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = opt.label,
                    color = if (active) colors.textPrimary else colors.textMuted,
                    fontSize = 13.sp,
                    fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                )
            }
        }
    }
}
