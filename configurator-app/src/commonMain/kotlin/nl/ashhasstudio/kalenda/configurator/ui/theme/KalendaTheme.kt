package nl.ashhasstudio.kalenda.configurator.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Stable
data class KalendaColors(
    val background: Color,
    val cardBackground: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val textSubtle: Color,
    val divider: Color,
    val rowHover: Color,
    val rowPress: Color,
    val isDark: Boolean,
)

val DarkColors = KalendaColors(
    background = Color(0xFF121214),
    cardBackground = Color(0xFF1C1C1E),
    textPrimary = Color.White,
    textMuted = Color.White.copy(alpha = 0.6f),
    textSubtle = Color(0xFF9E9E9E),
    divider = Color.White.copy(alpha = 0.08f),
    rowHover = Color.White.copy(alpha = 0.04f),
    rowPress = Color.White.copy(alpha = 0.08f),
    isDark = true,
)

val LightColors = KalendaColors(
    background = Color(0xFFF2F2F7),
    cardBackground = Color.White,
    textPrimary = Color(0xFF1C1C1E),
    textMuted = Color.Black.copy(alpha = 0.6f),
    textSubtle = Color(0xFF6E6E73),
    divider = Color.Black.copy(alpha = 0.08f),
    rowHover = Color.Black.copy(alpha = 0.04f),
    rowPress = Color.Black.copy(alpha = 0.08f),
    isDark = false,
)

val AccentBlue = Color(0xFF4FC3F7)

object CalColors {
    val peacock = Color(0xFF4FC3F7)
    val blueberry = Color(0xFF5C6BC0)
    val lavender = Color(0xFF9575CD)
    val grape = Color(0xFFAB47BC)
    val flamingo = Color(0xFFEC6D95)
    val tomato = Color(0xFFE57373)
    val tangerine = Color(0xFFF6A356)
    val banana = Color(0xFFF6BF26)
    val basil = Color(0xFF33B679)
    val sage = Color(0xFF7CB342)
    val graphite = Color(0xFF9E9E9E)

    val all = listOf(peacock, blueberry, lavender, grape, flamingo, tomato, tangerine, banana, basil, sage, graphite)
}

val LocalKalendaColors = staticCompositionLocalOf { DarkColors }

private val hueMap = mapOf(
    "peacock" to CalColors.peacock,
    "blueberry" to CalColors.blueberry,
    "lavender" to CalColors.lavender,
    "grape" to CalColors.grape,
    "flamingo" to CalColors.flamingo,
    "tomato" to CalColors.tomato,
    "tangerine" to CalColors.tangerine,
    "banana" to CalColors.banana,
    "basil" to CalColors.basil,
    "sage" to CalColors.sage,
    "graphite" to CalColors.graphite,
)

fun accentColorForHue(hue: String): Color = hueMap[hue] ?: AccentBlue

@Composable
fun KalendaTheme(isDark: Boolean = true, content: @Composable () -> Unit) {
    val colors = if (isDark) DarkColors else LightColors
    CompositionLocalProvider(LocalKalendaColors provides colors) {
        content()
    }
}
