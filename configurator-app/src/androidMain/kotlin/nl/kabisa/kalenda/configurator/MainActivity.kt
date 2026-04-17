package nl.kabisa.kalenda.configurator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import nl.kabisa.kalenda.configurator.navigation.AppNavigation
import nl.kabisa.kalenda.configurator.ui.accounts.OAuthWebViewScreen
import nl.kabisa.kalenda.data.AndroidCalendarRepository
import nl.kabisa.kalenda.data.AndroidSettingsRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsRepo = AndroidSettingsRepository(this)
        val calendarRepo = AndroidCalendarRepository(this)

        setContent {
            var showOAuth by remember { mutableStateOf(false) }

            MaterialTheme {
                if (showOAuth) {
                    OAuthWebViewScreen(
                        clientId = "PLACEHOLDER_CLIENT_ID",
                        onCodeReceived = { code ->
                            showOAuth = false
                            // Token exchange will be wired in Task 9
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
