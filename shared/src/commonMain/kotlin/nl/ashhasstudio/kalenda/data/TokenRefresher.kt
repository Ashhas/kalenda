package nl.ashhasstudio.kalenda.data

import nl.ashhasstudio.kalenda.domain.GoogleAccount

sealed class TokenRefreshOutcome {
    data class Success(val account: GoogleAccount) : TokenRefreshOutcome()
    /** Refresh token is no longer valid (user revoked, expired, password change). User must re-auth. */
    object NeedsReauth : TokenRefreshOutcome()
    /** Transient failure (network, 5xx). Retry later. */
    data class Transient(val reason: String) : TokenRefreshOutcome()
}

interface TokenRefresher {
    suspend fun refresh(account: GoogleAccount): TokenRefreshOutcome
}
