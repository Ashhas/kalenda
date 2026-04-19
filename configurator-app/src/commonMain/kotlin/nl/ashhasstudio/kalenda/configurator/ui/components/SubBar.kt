package nl.ashhasstudio.kalenda.configurator.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalKalendaColors

@Composable
fun SubBar(title: String, onBack: () -> Unit) {
    val colors = LocalKalendaColors.current
    Row(
        modifier = Modifier.padding(start = 6.dp, end = 6.dp, top = 14.dp, bottom = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "‹",
            color = colors.textPrimary,
            fontSize = 28.sp,
            modifier = Modifier
                .clickable(onClick = onBack)
                .padding(end = 12.dp)
        )
        Text(
            text = title,
            color = colors.textPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.2).sp,
        )
    }
}
