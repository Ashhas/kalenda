package nl.ashhasstudio.kalenda.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import nl.ashhasstudio.kalenda.domain.GoogleAccount

class GoogleTokenRefresher(
    private val clientId: String,
    private val client: HttpClient = HttpClientProvider.instance
) : TokenRefresher {

    override suspend fun refreshAccount(account: GoogleAccount): GoogleAccount? {
        if (account.refreshToken.isBlank()) return null
        val response = client.submitForm(
            url = "https://oauth2.googleapis.com/token",
            formParameters = parameters {
                append("client_id", clientId)
                append("refresh_token", account.refreshToken)
                append("grant_type", "refresh_token")
            }
        )
        if (!response.status.isSuccess()) return null
        val tokenResponse: RefreshTokenResponse = response.body()
        return account.copy(
            accessToken = tokenResponse.accessToken,
            refreshToken = tokenResponse.refreshToken ?: account.refreshToken
        )
    }
}

@Serializable
private data class RefreshTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String? = null
)
