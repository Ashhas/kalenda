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
import kotlinx.coroutines.launch
import nl.ashhasstudio.kalenda.configurator.ui.components.ColorStrip
import nl.ashhasstudio.kalenda.configurator.ui.components.SubBar
import nl.ashhasstudio.kalenda.configurator.ui.components.WidgetCard
import nl.ashhasstudio.kalenda.configurator.ui.components.WidgetDivider
import nl.ashhasstudio.kalenda.configurator.ui.components.WidgetSectionHeader
import nl.ashhasstudio.kalenda.configurator.ui.components.WidgetToggleRow
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalKalendaColors
import nl.ashhasstudio.kalenda.configurator.ui.theme.LocalStrings
import nl.ashhasstudio.kalenda.configurator.ui.theme.Spacing
import nl.ashhasstudio.kalenda.configurator.ui.theme.accentColorForHue
import nl.ashhasstudio.kalenda.data.SettingsRepository
import nl.ashhasstudio.kalenda.domain.ThemeMode
import nl.ashhasstudio.kalenda.domain.WidgetSettings

@Composable
fun AppearanceScreen(
    settingsRepository: SettingsRepository,
    onBack: () -> Unit,
) {
    val settings by settingsRepository.observeSettings().collectAsState(initial = WidgetSettings())
    val scope = rememberCoroutineScope()
    val colors = LocalKalendaColors.current
    val strings = LocalStrings.current
    val accent = accentColorForHue(settings.accentHue)
    val isLight = settings.themeMode == ThemeMode.LIGHT

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.screenPadding)
    ) {
        SubBar(title = strings.appearanceTitle, onBack = onBack)

        WidgetCard {
            WidgetToggleRow(
                barColor = accent,
                title = strings.appearanceLightMode,
                subtitle = if (isLight) strings.appearanceLightModeOn else strings.appearanceLightModeOff,
                checked = isLight,
                onCheckedChange = { v ->
                    scope.launch {
                        settingsRepository.updateSettings(
                            settings.copy(themeMode = if (v) ThemeMode.LIGHT else ThemeMode.DARK)
                        )
                    }
                },
            )

            WidgetDivider()

            WidgetSectionHeader(label = strings.appearanceAccentColor)
            ColorStrip(
                selected = settings.accentHue,
                onSelect = { hue ->
                    scope.launch {
                        settingsRepository.updateSettings(settings.copy(accentHue = hue))
                    }
                },
            )
        }
    }
}
