package nl.ashhasstudio.kalenda.domain

import kotlinx.serialization.Serializable

@Serializable
data class WidgetSettings(
    val scrollDays: Int = 7,
    val allDayPosition: AllDayPosition = AllDayPosition.TOP,
    val accounts: List<GoogleAccount> = emptyList(),
    val showAccountColors: Boolean = false,
    val accentHue: String = "peacock",
)
