package nl.ashhasstudio.kalenda.configurator.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import nl.ashhasstudio.kalenda.configurator.ui.components.ApplyCTA
import nl.ashhasstudio.kalenda.configurator.ui.components.HomeHero
import nl.ashhasstudio.kalenda.configurator.ui.components.NavRow
import nl.ashhasstudio.kalenda.configurator.ui.components.PreviewStage
import nl.ashhasstudio.kalenda.configurator.ui.components.SectionLabel
import nl.ashhasstudio.kalenda.configurator.ui.components.WidgetCard
import nl.ashhasstudio.kalenda.configurator.ui.components.WidgetDivider
import nl.ashhasstudio.kalenda.configurator.ui.preview.WidgetPreviewCard
import nl.ashhasstudio.kalenda.configurator.ui.theme.AccentBlue
import nl.ashhasstudio.kalenda.configurator.ui.theme.CalColors
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalKalendaColors
import nl.ashhasstudio.kalenda.configurator.ui.theme.accentColorForHue
import nl.ashhasstudio.kalenda.data.CalendarRepository
import nl.ashhasstudio.kalenda.data.SettingsRepository
import nl.ashhasstudio.kalenda.domain.DayGroup
import nl.ashhasstudio.kalenda.domain.WidgetSettings
import nl.ashhasstudio.kalenda.usecase.GroupEventsByDayUseCase

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
    val groupUseCase = remember { GroupEventsByDayUseCase() }
    var dayGroups by remember { mutableStateOf(emptyList<DayGroup>()) }
    val deviceTz = TimeZone.currentSystemDefault()

    LaunchedEffect(settings) {
        val cache = calendarRepository.getCachedEvents()
        dayGroups = groupUseCase(cache.events, settings, Clock.System.now(), deviceTz)
    }

    val colors = LocalKalendaColors.current
    val accent = accentColorForHue(settings.accentHue)
    val now = Clock.System.now().toLocalDateTime(deviceTz)
    val dayOfWeek = now.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
    val dateLabel = "${now.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }} ${now.dayOfMonth}"

    val accountCount = settings.accounts.size
    val activeCalendarCount = settings.accounts.sumOf { acc -> acc.calendars.count { it.enabled } }
    val layoutSummary = "${settings.scrollDays} days · ${settings.allDayPosition.name.lowercase()}"
    val appearanceSummary = "Accent · ${settings.accentHue}"
    val calendarsSummary = "$accountCount account${if (accountCount != 1) "s" else ""} · " +
        "$activeCalendarCount calendar${if (activeCalendarCount != 1) "s" else ""}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 12.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            HomeHero(
                dayOfWeek = dayOfWeek,
                dateLabel = dateLabel,
                accentColor = accent,
            )
            SectionLabel(text = "Preview")
        }

        PreviewStage {
            WidgetPreviewCard(dayGroups = dayGroups, deviceTimezone = deviceTz)
        }

        Column(modifier = Modifier.padding(horizontal = 12.dp)) {
            SectionLabel(text = "Configure")
            WidgetCard {
                NavRow(
                    icon = "layout",
                    tint = accent,
                    title = "Layout",
                    meta = layoutSummary,
                    onClick = onNavigateToLayout,
                )
                WidgetDivider(startPadding = 60)
                NavRow(
                    icon = "appearance",
                    tint = CalColors.lavender,
                    title = "Appearance",
                    meta = appearanceSummary,
                    onClick = onNavigateToAppearance,
                )
                WidgetDivider(startPadding = 60)
                NavRow(
                    icon = "calendars",
                    tint = CalColors.basil,
                    title = "Calendars",
                    meta = calendarsSummary,
                    onClick = onNavigateToCalendars,
                )
            }

            ApplyCTA(accent = accent, onClick = onApplyToWidget)
        }
    }
}
