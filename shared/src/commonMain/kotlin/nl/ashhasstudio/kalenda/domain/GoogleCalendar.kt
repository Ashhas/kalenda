package nl.ashhasstudio.kalenda.domain

import kotlinx.serialization.Serializable

@Serializable
data class GoogleCalendar(
    val id: String,
    val accountId: String,
    val name: String,
    val color: Long,
    val enabled: Boolean = true,
    val showAllDay: Boolean = true,
    val primary: Boolean = false,
)
