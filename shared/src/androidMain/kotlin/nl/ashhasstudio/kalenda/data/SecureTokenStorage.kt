package nl.ashhasstudio.kalenda.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecureTokenStorage {
    @Volatile
    private var prefs: SharedPreferences? = null

    private fun get(context: Context): SharedPreferences {
        return prefs ?: synchronized(this) {
            prefs ?: createEncryptedPrefs(context.applicationContext).also { prefs = it }
        }
    }

    private fun createEncryptedPrefs(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            "kalenda_secure_tokens",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveTokens(context: Context, accountId: String, accessToken: String, refreshToken: String) {
        get(context).edit()
            .putString("${accountId}_access", accessToken)
            .putString("${accountId}_refresh", refreshToken)
            .apply()
    }

    fun getAccessToken(context: Context, accountId: String): String {
        return get(context).getString("${accountId}_access", "") ?: ""
    }

    fun getRefreshToken(context: Context, accountId: String): String {
        return get(context).getString("${accountId}_refresh", "") ?: ""
    }

    fun removeTokens(context: Context, accountId: String) {
        get(context).edit()
            .remove("${accountId}_access")
            .remove("${accountId}_refresh")
            .apply()
    }
}
