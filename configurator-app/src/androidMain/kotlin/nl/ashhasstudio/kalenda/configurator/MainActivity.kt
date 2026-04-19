package nl.ashhasstudio.kalenda.configurator

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import nl.ashhasstudio.kalenda.configurator.ui.theme.KalendaTheme
import nl.ashhasstudio.kalenda.domain.ThemeMode
import nl.ashhasstudio.kalenda.domain.WidgetSettings
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import nl.ashhasstudio.kalenda.configurator.auth.TokenExchangeService
import nl.ashhasstudio.kalenda.data.GoogleTokenRefresher
import nl.ashhasstudio.kalenda.configurator.navigation.AppNavigation
import nl.ashhasstudio.kalenda.configurator.ui.accounts.buildRedirectUri
import nl.ashhasstudio.kalenda.configurator.ui.accounts.launchOAuthFlow
import nl.ashhasstudio.kalenda.data.AndroidCalendarRepository
import nl.ashhasstudio.kalenda.data.AndroidSettingsRepository
import nl.ashhasstudio.kalenda.sync.WorkManagerScheduler
import nl.ashhasstudio.kalenda.usecase.FetchEventsUseCase

class MainActivity : ComponentActivity() {

    private lateinit var settingsRepo: AndroidSettingsRepository
    private lateinit var calendarRepo: AndroidCalendarRepository
    private lateinit var clientId: String
    private lateinit var tokenService: TokenExchangeService
    private lateinit var tokenRefresher: GoogleTokenRefresher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        settingsRepo = AndroidSettingsRepository(this)
        calendarRepo = AndroidCalendarRepository(this)
        clientId = BuildConfig.GOOGLE_OAUTH_CLIENT_ID
        tokenService = TokenExchangeService(clientId)
        tokenRefresher = GoogleTokenRefresher(clientId)

        handleOAuthRedirect(intent)

        setContent {
            val settings by settingsRepo.observeSettings().collectAsState(initial = WidgetSettings())
            val isDark = settings.themeMode == ThemeMode.DARK
            androidx.compose.runtime.LaunchedEffect(isDark) {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !isDark
                    isAppearanceLightNavigationBars = !isDark
                }
                val bgColor = if (isDark) 0xFF121214.toInt() else 0xFFF2F2F7.toInt()
                window.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(bgColor))
            }
            KalendaTheme(isDark = isDark) {
                AppNavigation(
                    settingsRepository = settingsRepo,
                    calendarRepository = calendarRepo,
                    onAddAccount = { launchOAuthFlow(this@MainActivity, clientId) },
                    onApplyToWidget = {
                        lifecycleScope.launch { applyToWidget() }
                    },
                    onCacheRefresh = {
                        lifecycleScope.launch { refreshCacheOnly() }
                    },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleOAuthRedirect(intent)
    }

    private fun handleOAuthRedirect(intent: Intent?) {
        val data = intent?.data ?: return
        if (data.scheme?.startsWith("com.googleusercontent.apps.") != true) return

        // Clear the intent data so it's not re-processed on Activity recreation
        setIntent(Intent())

        val code = data.getQueryParameter("code")
        val error = data.getQueryParameter("error")

        if (error != null) {
            Log.w("Kalenda", "OAuth error: $error")
            Toast.makeText(this, "Authentication failed: $error", Toast.LENGTH_LONG).show()
            return
        }

        if (code != null) {
            Log.d("Kalenda", "Received OAuth code via redirect")
            lifecycleScope.launch {
                completeOAuthFlow(code)
            }
        }
    }

    private suspend fun applyToWidget() {
        try {
            val fetchUseCase = FetchEventsUseCase(calendarRepo, settingsRepo, tokenRefresher)
            fetchUseCase()
        } catch (e: Exception) {
            Log.w("Kalenda", "Fetch on apply failed, using cache", e)
        }
        refreshWidget(this@MainActivity)
        Toast.makeText(this, "Widget updated", Toast.LENGTH_SHORT).show()
    }

    private suspend fun refreshCacheOnly() {
        try {
            val fetchUseCase = FetchEventsUseCase(calendarRepo, settingsRepo, tokenRefresher)
            fetchUseCase()
        } catch (e: Exception) {
            Log.w("Kalenda", "Silent cache refresh failed", e)
        }
    }

    private suspend fun completeOAuthFlow(authCode: String) {
        try {
            val redirectUri = buildRedirectUri(clientId)
            val account = tokenService.exchangeCode(authCode, redirectUri)
            settingsRepo.addAccount(account)

            val fetchUseCase = FetchEventsUseCase(calendarRepo, settingsRepo, tokenRefresher)
            fetchUseCase()

            refreshWidget(this@MainActivity)
            WorkManagerScheduler.schedulePeriodicSync(this@MainActivity, clientId)

            Toast.makeText(this, "Added ${account.email}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("Kalenda", "Token exchange failed", e)
            Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
