package nl.ashhasstudio.kalenda.configurator.ui.accounts

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import nl.ashhasstudio.kalenda.configurator.R
import java.security.MessageDigest
import java.security.SecureRandom

private const val SCOPE = "https://www.googleapis.com/auth/calendar.readonly https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile"

private const val OAUTH_PREFS = "kalenda_oauth_state"
private const val KEY_CODE_VERIFIER = "code_verifier"
private const val KEY_STATE = "state"

fun buildRedirectUri(clientId: String): String {
    val reverseClientId = clientId.split(".").reversed().joinToString(".")
    return "$reverseClientId:/oauthredirect"
}

fun launchOAuthFlow(context: Context, clientId: String) {
    val redirectUri = buildRedirectUri(clientId)
    val verifier = generateCodeVerifier()
    val challenge = codeChallengeS256(verifier)
    val state = generateState()

    prefs(context).edit()
        .putString(KEY_CODE_VERIFIER, verifier)
        .putString(KEY_STATE, state)
        .apply()

    val authUrl = "https://accounts.google.com/o/oauth2/v2/auth" +
        "?client_id=$clientId" +
        "&redirect_uri=${Uri.encode(redirectUri)}" +
        "&response_type=code" +
        "&scope=${Uri.encode(SCOPE)}" +
        "&access_type=offline" +
        "&prompt=consent" +
        "&code_challenge=$challenge" +
        "&code_challenge_method=S256" +
        "&state=$state"

    val uri = Uri.parse(authUrl)
    try {
        CustomTabsIntent.Builder().build().launchUrl(context, uri)
    } catch (e: ActivityNotFoundException) {
        // No browser with Custom Tabs support — fall back to generic VIEW intent.
        Log.w("KalendaOAuth", "Custom Tabs unavailable, falling back to ACTION_VIEW", e)
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e2: ActivityNotFoundException) {
            Log.e("KalendaOAuth", "No browser available at all", e2)
            Toast.makeText(context, context.getString(R.string.auth_no_browser), Toast.LENGTH_LONG).show()
        }
    }
}

fun consumeOAuthState(context: Context): OAuthState {
    val p = prefs(context)
    val verifier = p.getString(KEY_CODE_VERIFIER, null).orEmpty()
    val state = p.getString(KEY_STATE, null).orEmpty()
    p.edit().remove(KEY_CODE_VERIFIER).remove(KEY_STATE).apply()
    return OAuthState(codeVerifier = verifier, state = state)
}

data class OAuthState(val codeVerifier: String, val state: String)

private fun prefs(context: Context): SharedPreferences =
    context.getSharedPreferences(OAUTH_PREFS, Context.MODE_PRIVATE)

private fun generateCodeVerifier(): String {
    val bytes = ByteArray(32)
    SecureRandom().nextBytes(bytes)
    return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
}

private fun codeChallengeS256(verifier: String): String {
    val hash = MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray(Charsets.US_ASCII))
    return Base64.encodeToString(hash, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
}

private fun generateState(): String {
    val bytes = ByteArray(16)
    SecureRandom().nextBytes(bytes)
    return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
}
