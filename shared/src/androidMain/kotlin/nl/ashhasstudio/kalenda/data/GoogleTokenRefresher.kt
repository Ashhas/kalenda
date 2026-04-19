package nl.ashhasstudio.kalenda.data

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.ashhasstudio.kalenda.domain.GoogleAccount

class GoogleTokenRefresher(
    private val clientId: String,
    private val client: HttpClient = HttpClientProvider.instance
) : TokenRefresher {

    override suspend fun refresh(account: GoogleAccount): TokenRefreshOutcome {
        if (account.refreshToken.isBlank()) return TokenRefreshOutcome.NeedsReauth
        val response = try {
            client.submitForm(
                url = "https://oauth2.googleapis.com/token",
                formParameters = parameters {
                    append("client_id", clientId)
                    append("refresh_token", account.refreshToken)
                    append("grant_type", "refresh_token")
                }
            )
        } catch (e: Exception) {
            Log.w("KalendaTokenRefresh", "Network error refreshing ${account.email}", e)
            return TokenRefreshOutcome.Transient("network: ${e.message}")
        }

        return when {
            response.status.isSuccess() -> {
                val body: RefreshTokenResponse = response.body()
                TokenRefreshOutcome.Success(
                    account.copy(
                        accessToken = body.accessToken,
                        refreshToken = body.refreshToken ?: account.refreshToken,
                        needsReauth = false,
                    )
                )
            }
            // 4xx from Google's token endpoint almost always means invalid_grant
            // (revoked, expired, user changed password). Permanent without re-auth.
            response.status.value in 400..499 -> {
                val body = runCatching { response.bodyAsText() }.getOrDefault("")
                Log.w("KalendaTokenRefresh", "Permanent refresh failure for ${account.email}: ${response.status} $body")
                TokenRefreshOutcome.NeedsReauth
            }
            else -> {
                Log.w("KalendaTokenRefresh", "Transient refresh failure ${response.status} for ${account.email}")
                TokenRefreshOutcome.Transient("http: ${response.status}")
            }
        }
    }
}

@Serializable
private data class RefreshTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String? = null
)
