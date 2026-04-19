package nl.ashhasstudio.kalenda.domain

import kotlinx.serialization.Serializable

@Serializable
data class WidgetSettings(
    val scrollDays: Int = 7,
    val dayMode: DayMode = DayMode.ROLLING,
    val allDayPosition: AllDayPosition = AllDayPosition.TOP,
    val accounts: List<GoogleAccount> = emptyList(),
    val accentHue: String = "peacock",
    val themeMode: ThemeMode = ThemeMode.DARK,
)
