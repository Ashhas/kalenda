package nl.ashhasstudio.kalenda.widget.ui

import androidx.compose.ui.graphics.Color
import nl.ashhasstudio.kalenda.domain.ThemeMode

data class WidgetTheme(
    val background: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val textSubtle: Color,
    val overlayBlack: Float, // pill tint alpha multiplier, keeps event colors legible
)

val DarkWidgetTheme = WidgetTheme(
    background = Color(0xFF1C1C1E),
    textPrimary = Color.White,
    textMuted = Color(0x99FFFFFF),
    textSubtle = Color(0xFF9E9E9E),
    overlayBlack = 0f,
)

val LightWidgetTheme = WidgetTheme(
    background = Color(0xFFFFFFFF),
    textPrimary = Color(0xFF1C1C1E),
    textMuted = Color(0x991C1C1E),
    textSubtle = Color(0xFF6E6E73),
    overlayBlack = 0f,
)

fun themeFor(mode: ThemeMode): WidgetTheme = when (mode) {
    ThemeMode.LIGHT -> LightWidgetTheme
    ThemeMode.DARK -> DarkWidgetTheme
}
