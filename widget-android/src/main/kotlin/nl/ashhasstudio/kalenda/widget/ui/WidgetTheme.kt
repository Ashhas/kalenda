package nl.ashhasstudio.kalenda.widget.ui

import androidx.compose.ui.graphics.Color
import nl.ashhasstudio.kalenda.domain.ThemeMode

data class WidgetTheme(
    val background: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val textSubtle: Color,
    val accent: Color,
)

val DarkWidgetTheme = WidgetTheme(
    background = Color(0xFF1C1C1E),
    textPrimary = Color.White,
    textMuted = Color(0x99FFFFFF),
    textSubtle = Color(0xFF9E9E9E),
    accent = Color(0xFF4FC3F7),
)

val LightWidgetTheme = WidgetTheme(
    background = Color(0xFFFFFFFF),
    textPrimary = Color(0xFF1C1C1E),
    textMuted = Color(0x991C1C1E),
    textSubtle = Color(0xFF6E6E73),
    accent = Color(0xFF4FC3F7),
)

fun themeFor(mode: ThemeMode): WidgetTheme = when (mode) {
    ThemeMode.LIGHT -> LightWidgetTheme
    ThemeMode.DARK, ThemeMode.SYSTEM -> DarkWidgetTheme
}

// Calendar accent palette — keep in sync with CalColors in the configurator module.
private val accentPalette = mapOf(
    "peacock" to Color(0xFF4FC3F7),
    "blueberry" to Color(0xFF5C6BC0),
    "lavender" to Color(0xFF9575CD),
    "grape" to Color(0xFFAB47BC),
    "flamingo" to Color(0xFFEC6D95),
    "tomato" to Color(0xFFE57373),
    "tangerine" to Color(0xFFF6A356),
    "banana" to Color(0xFFF6BF26),
    "basil" to Color(0xFF33B679),
    "sage" to Color(0xFF7CB342),
    "graphite" to Color(0xFF9E9E9E),
)

fun accentFor(hue: String): Color = accentPalette[hue] ?: Color(0xFF4FC3F7)
