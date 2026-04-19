package nl.ashhasstudio.kalenda.configurator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalKalendaColors

@Composable
fun WidgetToggleRow(
    barColor: Color,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val colors = LocalKalendaColors.current
    Row(
        modifier = Modifier
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(30.dp)
                .clip(RoundedCornerShape(1.5.dp))
                .background(
                    if (checked) barColor
                    else if (colors.isDark) Color.White.copy(alpha = 0.18f)
                    else Color.Black.copy(alpha = 0.14f)
                )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (checked) colors.textPrimary else colors.textMuted,
                fontSize = 14.sp,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = colors.textSubtle,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        KalendaSwitch(checked = checked, onCheckedChange = onCheckedChange, accent = barColor)
    }
}
