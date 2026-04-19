package nl.ashhasstudio.kalenda.configurator.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.ashhasstudio.kalenda.configurator.ui.theme.FontSizes
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalKalendaColors
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalStrings
import nl.ashhasstudio.kalenda.configurator.ui.theme.Shapes

private const val PRESSED_SCALE = 0.97f
private const val PRESSED_ALPHA = 0.85f
private const val PRESS_ANIM_MS = 80
private val CtaDarkTextOnAccent = Color(0xFF0B1220)

@Composable
fun ApplyCTA(
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val labelColor = if (LocalKalendaColors.current.isDark) CtaDarkTextOnAccent else Color.White
    val strings = LocalStrings.current
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) PRESSED_SCALE else 1f,
        animationSpec = tween(PRESS_ANIM_MS)
    )
    val alpha by animateFloatAsState(
        targetValue = if (pressed) PRESSED_ALPHA else 1f,
        animationSpec = tween(PRESS_ANIM_MS)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .graphicsLayer { this.alpha = alpha }
            .clip(RoundedCornerShape(Shapes.buttonRadius))
            .background(accent)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        try {
                            awaitRelease()
                        } finally {
                            pressed = false
                        }
                        onClick()
                    }
                )
            }
            .padding(vertical = 14.dp, horizontal = 18.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = strings.applyToWidget,
            color = labelColor,
            fontSize = FontSizes.primary,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.1.sp,
        )
        Text(
            text = "  \u2192",
            color = labelColor,
            fontSize = FontSizes.primary,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
