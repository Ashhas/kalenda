package nl.ashhasstudio.kalenda.configurator.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import nl.ashhasstudio.kalenda.data.EventCache
import nl.ashhasstudio.kalenda.configurator.ui.components.ApplyCTA
import nl.ashhasstudio.kalenda.configurator.ui.components.HomeHero
import nl.ashhasstudio.kalenda.configurator.ui.components.NavRow
import nl.ashhasstudio.kalenda.configurator.ui.components.PreviewStage
import nl.ashhasstudio.kalenda.configurator.ui.components.SectionLabel
import nl.ashhasstudio.kalenda.configurator.ui.components.WidgetCard
import nl.ashhasstudio.kalenda.configurator.ui.components.WidgetDivider
import nl.ashhasstudio.kalenda.configurator.ui.preview.WidgetPreviewCard
import nl.ashhasstudio.kalenda.configurator.ui.theme.CalColors
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalKalendaColors
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalStrings
import nl.ashhasstudio.kalenda.configurator.ui.theme.Spacing
import nl.ashhasstudio.kalenda.configurator.ui.theme.accentColorForHue
import nl.ashhasstudio.kalenda.data.CalendarRepository
import nl.ashhasstudio.kalenda.data.SettingsRepository
import nl.ashhasstudio.kalenda.domain.DayMode
import nl.ashhasstudio.kalenda.domain.WidgetSettings
import nl.ashhasstudio.kalenda.usecase.GroupEventsByDayUseCase

// Icon badge color tints (not tokens — these are intentionally different per section).
private const val ICON_NAME_LAYOUT = "layout"
private const val ICON_NAME_APPEARANCE = "appearance"
private const val ICON_NAME_CALENDARS = "calendars"
private const val NAV_DIVIDER_START_PADDING = 60

@Composable
fun HomeScreen(
    settingsRepository: SettingsRepository,
    calendarRepository: CalendarRepository,
    onNavigateToLayout: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    onNavigateToCalendars: () -> Unit,
    onApplyToWidget: () -> Unit,
) {
    val settings by settingsRepository.observeSettings().collectAsState(initial = WidgetSettings())
    val cache by calendarRepository.observeCachedEvents().collectAsState(initial = EventCache())
    val groupUseCase = remember { GroupEventsByDayUseCase() }
    val deviceTz = TimeZone.currentSystemDefault()

    val dayGroups = remember(cache, settings) {
        groupUseCase(cache.events, settings, Clock.System.now(), deviceTz)
    }

    val colors = LocalKalendaColors.current
    val strings = LocalStrings.current
    val accent = accentColorForHue(settings.accentHue)
    val now = Clock.System.now().toLocalDateTime(deviceTz)
    val dayOfWeek = now.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
    val dateLabel = "${now.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }} ${now.dayOfMonth}"

    val accountCount = settings.accounts.size
    val activeCalendarCount = settings.accounts.sumOf { acc -> acc.calendars.count { it.enabled } }
    val dayRangeLabel = when (settings.dayMode) {
        DayMode.THIS_WEEK -> strings.homeDayRangeThisWeek
        DayMode.ROLLING -> strings.homeDayRangeRolling(settings.scrollDays)
    }
    val layoutSummary = "$dayRangeLabel · ${settings.allDayPosition.name.lowercase()}"
    val appearanceSummary = strings.homeAccentSummary(settings.accentHue)
    val calendarsSummary = strings.homeAccountsSummary(accountCount, activeCalendarCount)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = Spacing.screenPadding, bottom = Spacing.scrollableBottomPadding)
        ) {
            Column(modifier = Modifier.padding(horizontal = Spacing.screenPadding)) {
                HomeHero(
                    dayOfWeek = dayOfWeek,
                    dateLabel = dateLabel,
                    accentColor = accent,
                )
                SectionLabel(text = strings.homePreviewLabel)
            }

            PreviewStage {
                WidgetPreviewCard(dayGroups = dayGroups, deviceTimezone = deviceTz)
            }

            Column(modifier = Modifier.padding(horizontal = Spacing.screenPadding)) {
                SectionLabel(text = strings.homeConfigureLabel)
                WidgetCard {
                    NavRow(
                        icon = ICON_NAME_LAYOUT,
                        tint = accent,
                        title = strings.homeNavLayout,
                        meta = layoutSummary,
                        onClick = onNavigateToLayout,
                    )
                    WidgetDivider(startPadding = NAV_DIVIDER_START_PADDING)
                    NavRow(
                        icon = ICON_NAME_APPEARANCE,
                        tint = CalColors.lavender,
                        title = strings.homeNavAppearance,
                        meta = appearanceSummary,
                        onClick = onNavigateToAppearance,
                    )
                    WidgetDivider(startPadding = NAV_DIVIDER_START_PADDING)
                    NavRow(
                        icon = ICON_NAME_CALENDARS,
                        tint = CalColors.basil,
                        title = strings.homeNavCalendars,
                        meta = calendarsSummary,
                        onClick = onNavigateToCalendars,
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = Spacing.screenPadding, vertical = Spacing.screenPadding)) {
            ApplyCTA(accent = accent, onClick = onApplyToWidget)
        }
    }
}
