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
import nl.ashhasstudio.kalenda.configurator.ui.theme.androidStrings
import nl.ashhasstudio.kalenda.domain.ThemeMode
import nl.ashhasstudio.kalenda.domain.WidgetSettings
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import nl.ashhasstudio.kalenda.configurator.auth.TokenExchangeService
import nl.ashhasstudio.kalenda.data.GoogleTokenRefresher
import nl.ashhasstudio.kalenda.configurator.navigation.AppNavigation
import nl.ashhasstudio.kalenda.configurator.ui.accounts.buildRedirectUri
import nl.ashhasstudio.kalenda.configurator.ui.accounts.consumeOAuthState
import nl.ashhasstudio.kalenda.configurator.ui.accounts.launchOAuthFlow
import nl.ashhasstudio.kalenda.data.AndroidCalendarRepository
import nl.ashhasstudio.kalenda.data.AndroidSettingsRepository
import nl.ashhasstudio.kalenda.sync.schedulePeriodicSync
import nl.ashhasstudio.kalenda.usecase.FetchEventsUseCase
import nl.ashhasstudio.kalenda.usecase.FetchOutcome

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

        // Reschedule periodic sync on every launch so clearing WorkManager DB or app data
        // still recovers on next open. enqueueUniquePeriodicWork(UPDATE) is idempotent.
        schedulePeriodicSync(this, clientId)

        handleOAuthRedirect(intent)

        setContent {
            val settings by settingsRepo.observeSettings().collectAsState(initial = WidgetSettings())
            val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
            val isDark = when (settings.themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> systemDark
            }
            androidx.compose.runtime.LaunchedEffect(isDark) {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !isDark
                    isAppearanceLightNavigationBars = !isDark
                }
                val bgColor = if (isDark) 0xFF121214.toInt() else 0xFFF2F2F7.toInt()
                window.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(bgColor))
            }
            KalendaTheme(isDark = isDark, strings = androidStrings()) {
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
        val returnedState = data.getQueryParameter("state")

        if (error != null) {
            Log.w("Kalenda", "OAuth error: $error")
            Toast.makeText(this, getString(R.string.auth_failed_with_error, error), Toast.LENGTH_LONG).show()
            return
        }

        if (code != null) {
            val oauthState = consumeOAuthState(this)
            if (oauthState.state.isNotEmpty() && oauthState.state != returnedState) {
                Log.w("Kalenda", "OAuth state mismatch — possible replay/spoof")
                Toast.makeText(this, getString(R.string.auth_failed_state_mismatch), Toast.LENGTH_LONG).show()
                return
            }
            Log.d("Kalenda", "Received OAuth code via redirect")
            lifecycleScope.launch {
                completeOAuthFlow(code, oauthState.codeVerifier)
            }
        }
    }

    private suspend fun applyToWidget() {
        val fetchUseCase = FetchEventsUseCase(calendarRepo, settingsRepo, tokenRefresher)
        val result = runCatching { fetchUseCase() }
        refreshWidget(this@MainActivity)

        val message = result.fold(
            onSuccess = { outcomes -> messageForOutcomes(outcomes) },
            onFailure = { err ->
                Log.w("Kalenda", "Fetch on apply failed", err)
                if (err is java.io.IOException ||
                    err.message?.contains("connect", ignoreCase = true) == true
                ) getString(R.string.toast_widget_offline)
                else getString(R.string.toast_widget_cant_refresh)
            }
        )
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun messageForOutcomes(outcomes: List<FetchOutcome>): String {
        if (outcomes.isEmpty()) return getString(R.string.toast_widget_updated)
        val anyOk = outcomes.any { it is FetchOutcome.Ok }
        val needsReauth = outcomes.any { it is FetchOutcome.NeedsReauth }
        val anyFailed = outcomes.any { it is FetchOutcome.Failed }
        return when {
            anyOk && !needsReauth && !anyFailed -> getString(R.string.toast_widget_updated)
            anyOk -> getString(R.string.toast_widget_updated)
            needsReauth -> getString(R.string.toast_widget_cant_refresh)
            else -> getString(R.string.toast_widget_cant_refresh)
        }
    }

    private suspend fun refreshCacheOnly() {
        runCatching {
            FetchEventsUseCase(calendarRepo, settingsRepo, tokenRefresher).invoke()
        }.onFailure { Log.w("Kalenda", "Silent cache refresh failed", it) }
        // Refresh the widget even if some accounts failed — cache may still have useful
        // partial data, and Glance won't pick up changes without an explicit update call.
        runCatching { refreshWidget(this@MainActivity) }
            .onFailure { Log.w("Kalenda", "refreshWidget after cache refresh failed", it) }
    }

    private suspend fun completeOAuthFlow(authCode: String, codeVerifier: String) {
        try {
            val redirectUri = buildRedirectUri(clientId)
            val account = tokenService.exchangeCode(authCode, redirectUri, codeVerifier)
            settingsRepo.addAccount(account)

            val fetchUseCase = FetchEventsUseCase(calendarRepo, settingsRepo, tokenRefresher)
            fetchUseCase()

            refreshWidget(this@MainActivity)

            Toast.makeText(this, getString(R.string.auth_account_added, account.email), Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("Kalenda", "Token exchange failed", e)
            Toast.makeText(this, getString(R.string.auth_exchange_failed, e.message ?: ""), Toast.LENGTH_LONG).show()
        }
    }
}
