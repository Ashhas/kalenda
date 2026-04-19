package nl.ashhasstudio.kalenda.configurator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.ashhasstudio.kalenda.configurator.ui.theme.FontSizes
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalKalendaColors
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalStrings

private val WordmarkSize = 26.sp
private val AccentDotSize = 7.dp

@Composable
fun HomeHero(
    dayOfWeek: String,
    dateLabel: String,
    accentColor: Color,
) {
    val colors = LocalKalendaColors.current
    val strings = LocalStrings.current
    Row(
        modifier = Modifier.padding(start = 6.dp, end = 6.dp, top = 16.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = strings.appName,
            color = colors.textPrimary,
            fontSize = WordmarkSize,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.2).sp,
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(AccentDotSize)
                .clip(CircleShape)
                .background(accentColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$dayOfWeek · $dateLabel".uppercase(),
            color = colors.textMuted,
            fontSize = FontSizes.small,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.4.sp,
        )
    }
}
