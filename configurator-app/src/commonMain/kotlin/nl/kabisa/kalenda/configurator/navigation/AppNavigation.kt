package nl.kabisa.kalenda.configurator.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import nl.kabisa.kalenda.configurator.ui.accounts.AccountsScreen
import nl.kabisa.kalenda.configurator.ui.home.HomeScreen
import nl.kabisa.kalenda.configurator.ui.settings.WidgetSettingsScreen
import nl.kabisa.kalenda.data.CalendarRepository
import nl.kabisa.kalenda.data.SettingsRepository

@Composable
fun AppNavigation(
    settingsRepository: SettingsRepository,
    calendarRepository: CalendarRepository,
    onAddAccount: () -> Unit
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                settingsRepository = settingsRepository,
                calendarRepository = calendarRepository,
                onNavigateToAccounts = { navController.navigate("accounts") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("accounts") {
            AccountsScreen(
                settingsRepository = settingsRepository,
                onAddAccount = onAddAccount
            )
        }
        composable("settings") {
            WidgetSettingsScreen(settingsRepository = settingsRepository)
        }
    }
}
