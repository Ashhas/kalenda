package nl.kabisa.kalenda.configurator.auth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import nl.kabisa.kalenda.domain.GoogleAccount

class TokenExchangeService(private val clientId: String) {

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }

    suspend fun exchangeCode(authCode: String): GoogleAccount {
        val tokenResponse: TokenResponse = client.submitForm(
            url = "https://oauth2.googleapis.com/token",
            formParameters = parameters {
                append("code", authCode)
                append("client_id", clientId)
                append("redirect_uri", "nl.kabisa.kalenda:/oauth2callback")
                append("grant_type", "authorization_code")
            }
        ).body()

        val userInfo: UserInfoResponse = client.get("https://www.googleapis.com/oauth2/v2/userinfo") {
            header("Authorization", "Bearer ${tokenResponse.accessToken}")
        }.body()

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
