package nl.ashhasstudio.kalenda.configurator.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * All user-visible English strings, injected via CompositionLocal.
 *
 * Resolved at the Android entry point from `strings.xml` (see `MainActivity.kt`). Keeps
 * composables in `commonMain` without an Android resource dependency and gives us a
 * single place to swap for i18n later.
 *
 * Parameterized strings are functions so callers don't format strings in composables.
 */
data class Strings(
    val appName: String,

    // Home
    val homePreviewLabel: String,
    val homeConfigureLabel: String,
    val homeNavLayout: String,
    val homeNavAppearance: String,
    val homeNavCalendars: String,
    val homeDayRangeThisWeek: String,
    val homeDayRangeRolling: (days: Int) -> String,
    val homeAccentSummary: (hue: String) -> String,
    val homeAccountsSummary: (accounts: Int, calendars: Int) -> String,

    // Layout
    val layoutTitle: String,
    val layoutHeaderDaysLeft: String,
    val layoutHeaderDaysVisible: String,
    val layoutSubtitleThisWeek: String,
    val layoutSubtitleRolling: String,
    val layoutDateRange: String,
    val layoutOptionRolling: String,
    val layoutOptionThisWeek: String,
    val layoutThisWeekHint: String,
    val layoutAllDay: String,
    val allDayOptionTop: String,
    val allDayOptionBottom: String,
    val allDayOptionHidden: String,

    // Appearance
    val appearanceTitle: String,
    val appearanceLightMode: String,
    val appearanceLightModeOn: String,
    val appearanceLightModeOff: String,
    val appearanceAccentColor: String,

    // Calendars
    val calendarsTitle: String,
    val calendarsConnectedCount: (count: Int) -> String,
    val calendarsErrorGeneric: String,
    val calendarsErrorWithMessage: (msg: String) -> String,
    val calendarsNeedsReauth: String,
    val calendarsActiveSummary: (active: Int, total: Int) -> String,
    val calendarsAllDayHidden: String,
    val calendarsAddGoogle: String,
    val calendarsExplainerTitle: String,
    val calendarsExplainerTapRow: String,
    val calendarsExplainerTapRowBody: String,
    val calendarsExplainerTapSun: String,
    val calendarsExplainerTapSunBody: String,

    // Dialogs
    val accountRemoveTitle: String,
    val accountRemoveBody: (email: String) -> String,
    val accountRemoveConfirm: String,
    val accountRemoveCancel: String,

    // Apply CTA
    val applyToWidget: String,
)

val LocalStrings = staticCompositionLocalOf<Strings> {
    error("LocalStrings not provided — wrap your screen in KalendaTheme")
}
