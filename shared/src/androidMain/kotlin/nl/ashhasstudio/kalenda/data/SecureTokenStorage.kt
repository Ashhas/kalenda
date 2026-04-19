package nl.ashhasstudio.kalenda.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

private const val PREFS_NAME = "kalenda_secure_tokens"

/**
 * Encrypted token storage. If the Android keystore is unavailable (e.g. a corrupt keystore
 * after Backup restore), we attempt ONE reset, then fail closed — OAuth tokens must never
 * hit the disk in plaintext. The upstream effect is a re-auth prompt; no silent downgrade.
 */
object SecureTokenStorage {
    @Volatile
    private var prefs: SharedPreferences? = null

    private fun getOrNull(context: Context): SharedPreferences? {
        prefs?.let { return it }
        return synchronized(this) {
            prefs ?: createEncryptedPrefs(context.applicationContext)?.also { prefs = it }
        }
    }

    private fun createEncryptedPrefs(context: Context): SharedPreferences? {
        return try {
            createEncryptedPrefsInner(context)
        } catch (e: Exception) {
            Log.w("KalendaSecure", "EncryptedSharedPreferences init failed, resetting", e)
            context.deleteSharedPreferences(PREFS_NAME)
            runCatching { createEncryptedPrefsInner(context) }
                .onFailure { e2 ->
                    Log.e(
                        "KalendaSecure",
                        "Keystore unavailable after reset — tokens cannot be stored. User must re-auth.",
                        e2,
                    )
                }.getOrNull()
        }
    }

    private fun createEncryptedPrefsInner(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveTokens(context: Context, accountId: String, accessToken: String, refreshToken: String) {
        val p = getOrNull(context) ?: run {
            Log.e("KalendaSecure", "Cannot save tokens — encrypted storage unavailable")
            return
        }
        runCatching {
            p.edit()
                .putString("${accountId}_access", accessToken)
                .putString("${accountId}_refresh", refreshToken)
                .apply()
        }.onFailure { Log.w("KalendaSecure", "saveTokens failed", it) }
    }

    fun getAccessToken(context: Context, accountId: String): String =
        runCatching { getOrNull(context)?.getString("${accountId}_access", "") ?: "" }
            .getOrElse { "" }

    fun getRefreshToken(context: Context, accountId: String): String =
        runCatching { getOrNull(context)?.getString("${accountId}_refresh", "") ?: "" }
            .getOrElse { "" }

    fun removeTokens(context: Context, accountId: String) {
        val p = getOrNull(context) ?: return
        runCatching {
            p.edit()
                .remove("${accountId}_access")
                .remove("${accountId}_refresh")
                .apply()
        }.onFailure { Log.w("KalendaSecure", "removeTokens failed", it) }
    }
}
