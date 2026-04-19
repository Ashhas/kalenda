package nl.ashhasstudio.kalenda.configurator.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import nl.ashhasstudio.kalenda.configurator.R

/**
 * Resolves the [Strings] bag from Android resources. Called once at the top of the
 * Compose tree and provided via [LocalStrings].
 *
 * Uses `Context.getString(...)` rather than `stringResource(...)` so lambda factories
 * (which are not `@Composable`) can still build parameterised strings.
 */
@Composable
fun androidStrings(): Strings {
    val context = LocalContext.current
    return Strings(
        appName = context.getString(R.string.app_name),
        homePreviewLabel = context.getString(R.string.home_preview_label),
        homeConfigureLabel = context.getString(R.string.home_configure_label),
        homeNavLayout = context.getString(R.string.home_nav_layout),
        homeNavAppearance = context.getString(R.string.home_nav_appearance),
        homeNavCalendars = context.getString(R.string.home_nav_calendars),
        homeDayRangeThisWeek = context.getString(R.string.home_day_range_this_week),
        homeDayRangeRolling = { days -> context.getString(R.string.home_day_range_rolling, days) },
        homeAccentSummary = { hue -> context.getString(R.string.home_accent_summary, hue) },
        homeAccountsSummary = { accounts, calendars ->
            context.getString(
                R.string.home_accounts_summary,
                accounts, if (accounts != 1) "s" else "",
                calendars, if (calendars != 1) "s" else "",
            )
        },

        layoutTitle = context.getString(R.string.layout_title),
        layoutHeaderDaysLeft = context.getString(R.string.layout_header_days_left),
        layoutHeaderDaysVisible = context.getString(R.string.layout_header_days_visible),
        layoutSubtitleThisWeek = context.getString(R.string.layout_subtitle_this_week),
        layoutSubtitleRolling = context.getString(R.string.layout_subtitle_rolling),
        layoutDateRange = context.getString(R.string.layout_date_range),
        layoutOptionRolling = context.getString(R.string.layout_option_rolling),
        layoutOptionThisWeek = context.getString(R.string.layout_option_this_week),
        layoutThisWeekHint = context.getString(R.string.layout_this_week_hint),
        layoutAllDay = context.getString(R.string.layout_all_day),
        allDayOptionTop = context.getString(R.string.all_day_option_top),
        allDayOptionBottom = context.getString(R.string.all_day_option_bottom),
        allDayOptionHidden = context.getString(R.string.all_day_option_hidden),

        appearanceTitle = context.getString(R.string.appearance_title),
        appearanceLightMode = context.getString(R.string.appearance_light_mode),
        appearanceLightModeOn = context.getString(R.string.appearance_light_mode_on),
        appearanceLightModeOff = context.getString(R.string.appearance_light_mode_off),
        appearanceAccentColor = context.getString(R.string.appearance_accent_color),

        calendarsTitle = context.getString(R.string.calendars_title),
        calendarsConnectedCount = { count -> context.getString(R.string.calendars_connected_count, count) },
        calendarsErrorGeneric = context.getString(R.string.calendars_error_generic),
        calendarsErrorWithMessage = { msg -> context.getString(R.string.calendars_error_with_message, msg) },
        calendarsNeedsReauth = context.getString(R.string.calendars_needs_reauth),
        calendarsActiveSummary = { active, total ->
            context.getString(R.string.calendars_active_summary, active, total, if (total != 1) "s" else "")
        },
        calendarsAllDayHidden = context.getString(R.string.calendars_all_day_hidden),
        calendarsAddGoogle = context.getString(R.string.calendars_add_google),
        calendarsExplainerTitle = context.getString(R.string.calendars_explainer_title),
        calendarsExplainerTapRow = context.getString(R.string.calendars_explainer_tap_row),
        calendarsExplainerTapRowBody = context.getString(R.string.calendars_explainer_tap_row_body),
        calendarsExplainerTapSun = context.getString(R.string.calendars_explainer_tap_sun),
        calendarsExplainerTapSunBody = context.getString(R.string.calendars_explainer_tap_sun_body),

        accountRemoveTitle = context.getString(R.string.account_remove_title),
        accountRemoveBody = { email -> context.getString(R.string.account_remove_body, email) },
        accountRemoveConfirm = context.getString(R.string.account_remove_confirm),
        accountRemoveCancel = context.getString(R.string.account_remove_cancel),

        applyToWidget = context.getString(R.string.apply_to_widget),
    )
}
