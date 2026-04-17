package nl.kabisa.kalenda.domain

import kotlinx.serialization.Serializable

@Serializable
data class GoogleAccount(
    val id: String,
    val email: String,
    val accessToken: String,
    val refreshToken: String
)
