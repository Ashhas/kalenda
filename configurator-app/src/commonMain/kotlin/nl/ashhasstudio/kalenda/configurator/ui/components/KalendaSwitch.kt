package nl.ashhasstudio.kalenda.configurator.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import nl.ashhasstudio.kalenda.configurator.ui.theme.AccentBlue
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalKalendaColors

@Composable
fun KalendaSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accent: Color = AccentBlue,
) {
    val colors = LocalKalendaColors.current
    val offTrack = if (colors.isDark) Color.White.copy(alpha = 0.18f) else Color.Black.copy(alpha = 0.14f)
    val thumbOffset = animateDpAsState(
        targetValue = if (checked) 18.dp else 2.dp,
        animationSpec = tween(160)
    )
    Box(
        modifier = Modifier
            .size(width = 40.dp, height = 24.dp)
            .clip(CircleShape)
            .background(if (checked) accent else offTrack)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) }
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset.value, y = 2.dp)
                .size(20.dp)
                .shadow(3.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}
