package nl.ashhasstudio.kalenda.configurator.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import nl.ashhasstudio.kalenda.configurator.ui.home.HomeScreen
import nl.ashhasstudio.kalenda.configurator.ui.screens.AppearanceScreen
import nl.ashhasstudio.kalenda.configurator.ui.screens.CalendarsScreen
import nl.ashhasstudio.kalenda.configurator.ui.screens.LayoutScreen
import nl.ashhasstudio.kalenda.data.CalendarRepository
import nl.ashhasstudio.kalenda.data.SettingsRepository

private const val SLIDE_DURATION = 300

@Composable
fun AppNavigation(
    settingsRepository: SettingsRepository,
    calendarRepository: CalendarRepository,
    onAddAccount: () -> Unit,
    onApplyToWidget: () -> Unit = {},
    onCacheRefresh: () -> Unit = {},
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home",
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(SLIDE_DURATION)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(SLIDE_DURATION)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(SLIDE_DURATION)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(SLIDE_DURATION)
            )
        },
    ) {
        composable("home") {
            HomeScreen(
                settingsRepository = settingsRepository,
                calendarRepository = calendarRepository,
                onNavigateToLayout = { navController.navigate("layout") },
                onNavigateToAppearance = { navController.navigate("appearance") },
                onNavigateToCalendars = { navController.navigate("calendars") },
                onApplyToWidget = onApplyToWidget,
            )
        }
        composable("layout") {
            LayoutScreen(
                settingsRepository = settingsRepository,
                onBack = { navController.popBackStack() },
            )
        }
        composable("appearance") {
            AppearanceScreen(
                settingsRepository = settingsRepository,
                onBack = { navController.popBackStack() },
            )
        }
        composable("calendars") {
            CalendarsScreen(
                settingsRepository = settingsRepository,
                calendarRepository = calendarRepository,
                onAddAccount = onAddAccount,
                onCalendarsChanged = onCacheRefresh,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
