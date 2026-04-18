package nl.ashhasstudio.kalenda.configurator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalKalendaColors

private data class SizeOption(val value: String, val label: String, val w: Dp, val h: Dp)

private val sizeOptions = listOf(
    SizeOption("small", "Small", 22.dp, 22.dp),
    SizeOption("medium", "Medium", 34.dp, 22.dp),
    SizeOption("large", "Large", 34.dp, 34.dp),
)

@Composable
fun SizePicker(
    selected: String,
    onSelect: (String) -> Unit,
    accent: Color,
) {
    val colors = LocalKalendaColors.current
    Row(
        modifier = Modifier.padding(horizontal = 17.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        sizeOptions.forEach { opt ->
            val active = opt.value == selected
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (active) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.03f))
                    .then(
                        if (active) Modifier.border(1.dp, accent, RoundedCornerShape(10.dp))
                        else Modifier
                    )
                    .clickable { onSelect(opt.value) }
                    .padding(vertical = 12.dp, horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(opt.w, opt.h)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (active) accent else Color.White.copy(alpha = 0.2f))
                )
                Text(
                    text = opt.label,
                    color = if (active) colors.textPrimary else colors.textMuted,
                    fontSize = 12.sp,
                    fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                )
            }
        }
    }
}
