package nl.ashhasstudio.kalenda.configurator.auth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.ashhasstudio.kalenda.data.HttpClientProvider
import nl.ashhasstudio.kalenda.domain.GoogleAccount

class TokenExchangeService(private val clientId: String) {

    private val client: HttpClient = HttpClientProvider.instance

    suspend fun exchangeCode(authCode: String, redirectUri: String, codeVerifier: String): GoogleAccount {
        val tokenExchangeResponse = client.submitForm(
            url = "https://oauth2.googleapis.com/token",
            formParameters = parameters {
                append("code", authCode)
                append("client_id", clientId)
                append("redirect_uri", redirectUri)
                append("grant_type", "authorization_code")
                if (codeVerifier.isNotEmpty()) append("code_verifier", codeVerifier)
            }
        )
        if (!tokenExchangeResponse.status.isSuccess()) {
            val body = runCatching { tokenExchangeResponse.bodyAsText() }.getOrDefault("")
            throw Exception("Token exchange failed (${tokenExchangeResponse.status}): $body")
        }
        val tokenResponse: TokenResponse = tokenExchangeResponse.body()

        val userInfoResponse = client.get("https://www.googleapis.com/oauth2/v2/userinfo") {
            header("Authorization", "Bearer ${tokenResponse.accessToken}")
        }
        if (!userInfoResponse.status.isSuccess()) {
            throw Exception("Failed to fetch user info: ${userInfoResponse.status}")
        }
        val userInfo: UserInfoResponse = userInfoResponse.body()

        return GoogleAccount(
            id = userInfo.id,
            email = userInfo.email,
            accessToken = tokenResponse.accessToken,
            refreshToken = tokenResponse.refreshToken ?: ""
        )
    }
}

@Serializable
private data class TokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String? = null
)

@Serializable
private data class UserInfoResponse(val id: String, val email: String)
