package nl.kabisa.kalenda.configurator

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import nl.kabisa.kalenda.configurator.auth.TokenExchangeService
import nl.kabisa.kalenda.configurator.navigation.AppNavigation
import nl.kabisa.kalenda.configurator.ui.accounts.OAuthWebViewScreen
import nl.kabisa.kalenda.data.AndroidCalendarRepository
import nl.kabisa.kalenda.data.AndroidSettingsRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsRepo = AndroidSettingsRepository(this)
        val calendarRepo = AndroidCalendarRepository(this)
        val clientId = getString(R.string.google_oauth_client_id)
        val tokenExchangeService = TokenExchangeService(clientId)

        setContent {
            var showOAuth by remember { mutableStateOf(false) }
            val coroutineScope = rememberCoroutineScope()

            MaterialTheme {
                if (showOAuth) {
                    OAuthWebViewScreen(
                        clientId = clientId,
                        onCodeReceived = { code ->
                            showOAuth = false
                            coroutineScope.launch {
                                try {
                                    val account = tokenExchangeService.exchangeCode(code)
                                    settingsRepo.addAccount(account)
                                } catch (e: Exception) {
                                    Log.e("MainActivity", "Token exchange failed", e)
                                }
                            }
                        }
                    )
                } else {
                    AppNavigation(
                        settingsRepository = settingsRepo,
                        calendarRepository = calendarRepo,
                        onAddAccount = { showOAuth = true }
                    )
                }
            }
        }
    }
}
