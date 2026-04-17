package nl.kabisa.kalenda.configurator.ui.accounts

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

private const val REDIRECT_URI = "nl.kabisa.kalenda:/oauth2callback"
private const val SCOPE = "https://www.googleapis.com/auth/calendar.readonly"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun OAuthWebViewScreen(clientId: String, onCodeReceived: (String) -> Unit) {
    val authUrl = "https://accounts.google.com/o/oauth2/v2/auth" +
        "?client_id=$clientId" +
        "&redirect_uri=$REDIRECT_URI" +
        "&response_type=code" +
        "&scope=$SCOPE" +
        "&access_type=offline"

    AndroidView(factory = { context ->
        WebView(context).apply {
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    val url = request.url.toString()
                    if (url.startsWith(REDIRECT_URI)) {
                        val code = request.url.getQueryParameter("code")
                        if (code != null) onCodeReceived(code)
                        return true
                    }
                    return false
                }
            }
            loadUrl(authUrl)
        }
    })
}
