package nl.ashhasstudio.kalenda.configurator.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalKalendaColors

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = LocalKalendaColors.current.textSubtle,
        fontSize = 13.sp,
        modifier = modifier.padding(start = 18.dp, top = 10.dp, bottom = 8.dp)
    )
}

@Composable
fun WidgetSectionHeader(label: String, topSpace: Int = 14) {
    Text(
        text = label,
        color = LocalKalendaColors.current.textSubtle,
        fontSize = 12.sp,
        modifier = Modifier.padding(start = 17.dp, top = topSpace.dp, bottom = 4.dp)
    )
}
