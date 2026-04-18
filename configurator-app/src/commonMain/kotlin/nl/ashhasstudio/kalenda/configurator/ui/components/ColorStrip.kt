package nl.ashhasstudio.kalenda.configurator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import nl.ashhasstudio.kalenda.configurator.ui.theme.CalColors

data class AccentHue(val name: String, val color: Color)

val AccentHues = listOf(
    AccentHue("peacock", CalColors.peacock),
    AccentHue("blueberry", CalColors.blueberry),
    AccentHue("lavender", CalColors.lavender),
    AccentHue("grape", CalColors.grape),
    AccentHue("flamingo", CalColors.flamingo),
    AccentHue("tomato", CalColors.tomato),
    AccentHue("tangerine", CalColors.tangerine),
    AccentHue("banana", CalColors.banana),
    AccentHue("basil", CalColors.basil),
    AccentHue("sage", CalColors.sage),
    AccentHue("graphite", CalColors.graphite),
)

@Composable
fun ColorStrip(
    selected: String,
    onSelect: (String) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 17.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(AccentHues) { hue ->
            val active = hue.name == selected
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .scale(if (active) 1.1f else 1f)
                    .clip(CircleShape)
                    .background(hue.color)
                    .then(
                        if (active) Modifier.border(2.dp, Color.White, CircleShape)
                        else Modifier
                    )
                    .clickable { onSelect(hue.name) }
            )
        }
    }
}
