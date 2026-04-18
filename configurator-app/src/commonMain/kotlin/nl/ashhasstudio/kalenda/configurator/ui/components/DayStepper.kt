package nl.ashhasstudio.kalenda.configurator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DayStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    accent: Color,
    min: Int = 1,
    max: Int = 14,
) {
    val colors = LocalKalendaColors.current
    FlowRow(
        modifier = Modifier.padding(horizontal = 17.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        for (n in min..max) {
            val isCurrent = n == value
            val isActive = n <= value
            val bg = when {
                isCurrent -> accent
                isActive -> Color.White.copy(alpha = 0.08f)
                else -> Color.White.copy(alpha = 0.03f)
            }
            val textColor = when {
                isCurrent -> Color(0xFF0B1220)
                isActive -> colors.textPrimary
                else -> colors.textMuted
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(bg)
                    .clickable { onValueChange(n) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = n.toString(),
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                )
            }
        }
    }
}
