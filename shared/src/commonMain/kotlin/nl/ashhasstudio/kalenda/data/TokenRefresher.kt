package nl.ashhasstudio.kalenda.data

import nl.ashhasstudio.kalenda.domain.GoogleAccount

interface TokenRefresher {
    suspend fun refreshAccount(account: GoogleAccount): GoogleAccount?
}
