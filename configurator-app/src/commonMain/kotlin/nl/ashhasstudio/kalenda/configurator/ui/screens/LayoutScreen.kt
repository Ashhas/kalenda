package nl.ashhasstudio.kalenda.configurator.ui.screens

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import nl.ashhasstudio.kalenda.configurator.ui.components.DayStepper
import nl.ashhasstudio.kalenda.configurator.ui.components.SegmentOption
import nl.ashhasstudio.kalenda.configurator.ui.components.SegmentedControl
import nl.ashhasstudio.kalenda.configurator.ui.components.SubBar
import nl.ashhasstudio.kalenda.configurator.ui.components.WidgetCard
import nl.ashhasstudio.kalenda.configurator.ui.components.WidgetDivider
import nl.ashhasstudio.kalenda.configurator.ui.components.WidgetSectionHeader
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalKalendaColors
import nl.ashhasstudio.kalenda.configurator.ui.theme.accentColorForHue
import nl.ashhasstudio.kalenda.data.SettingsRepository
import nl.ashhasstudio.kalenda.domain.AllDayPosition
import nl.ashhasstudio.kalenda.domain.WidgetSettings

@Composable
fun LayoutScreen(
    settingsRepository: SettingsRepository,
    onBack: () -> Unit,
) {
    val settings by settingsRepository.observeSettings().collectAsState(initial = WidgetSettings())
    val scope = rememberCoroutineScope()
    val colors = LocalKalendaColors.current
    val accent = accentColorForHue(settings.accentHue)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        SubBar(title = "Layout", onBack = onBack)

        WidgetCard {
            WidgetSectionHeader(label = "Scroll range", topSpace = 14)
            DayStepper(
                value = settings.scrollDays,
                onValueChange = { days ->
                    scope.launch {
                        settingsRepository.updateSettings(settings.copy(scrollDays = days))
                    }
                },
                accent = accent,
            )

            WidgetDivider()

            WidgetSectionHeader(label = "All-day events")
            SegmentedControl(
                options = listOf(
                    SegmentOption(AllDayPosition.TOP, "Top"),
                    SegmentOption(AllDayPosition.BOTTOM, "Bottom"),
                    SegmentOption(AllDayPosition.HIDDEN, "Hidden"),
                ),
                selected = settings.allDayPosition,
                onSelect = { pos ->
                    scope.launch {
                        settingsRepository.updateSettings(settings.copy(allDayPosition = pos))
                    }
                },
            )
        }
    }
}
