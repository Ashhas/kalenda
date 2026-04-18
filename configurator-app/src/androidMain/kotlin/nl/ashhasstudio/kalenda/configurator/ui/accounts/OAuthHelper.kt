package nl.ashhasstudio.kalenda.configurator.ui.accounts

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

private const val SCOPE = "https://www.googleapis.com/auth/calendar.readonly https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile"

fun buildRedirectUri(clientId: String): String {
    val reverseClientId = clientId.split(".").reversed().joinToString(".")
    return "$reverseClientId:/oauthredirect"
}

fun launchOAuthFlow(context: Context, clientId: String) {
    val redirectUri = buildRedirectUri(clientId)
    val authUrl = "https://accounts.google.com/o/oauth2/v2/auth" +
        "?client_id=$clientId" +
        "&redirect_uri=${Uri.encode(redirectUri)}" +
        "&response_type=code" +
        "&scope=${java.net.URLEncoder.encode(SCOPE, "UTF-8")}" +
        "&access_type=offline" +
        "&prompt=consent"

    val customTabsIntent = CustomTabsIntent.Builder().build()
    customTabsIntent.launchUrl(context, Uri.parse(authUrl))
}
