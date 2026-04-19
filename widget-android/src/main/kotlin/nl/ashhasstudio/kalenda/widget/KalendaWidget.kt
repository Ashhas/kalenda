package nl.ashhasstudio.kalenda.widget

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.currentState
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import nl.ashhasstudio.kalenda.data.AndroidCalendarRepository
import nl.ashhasstudio.kalenda.data.AndroidSettingsRepository
import nl.ashhasstudio.kalenda.domain.DayGroup
import nl.ashhasstudio.kalenda.domain.WidgetSettings
import nl.ashhasstudio.kalenda.usecase.GroupEventsByDayUseCase
import nl.ashhasstudio.kalenda.widget.ui.DarkWidgetTheme
import nl.ashhasstudio.kalenda.widget.ui.WidgetContent
import nl.ashhasstudio.kalenda.widget.ui.WidgetErrorContent
import nl.ashhasstudio.kalenda.widget.ui.accentFor
import nl.ashhasstudio.kalenda.widget.ui.themeFor

class KalendaWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val result = runCatching { buildViewModel(context) }
        provideContent {
            currentState<Preferences>()

            val size = LocalSize.current
            val isScrollable = size.height.value > 200f

            result.fold(
                onSuccess = { vm ->
                    WidgetContent(
                        dayGroups = vm.dayGroups,
                        deviceTimezone = vm.deviceTz,
                        isScrollable = isScrollable,
                        theme = vm.theme,
                    )
                },
                onFailure = { e ->
                    Log.e("KalendaWidget", "Failed to render widget", e)
                    WidgetErrorContent(theme = DarkWidgetTheme)
                }
            )
        }
    }

    private suspend fun buildViewModel(context: Context): WidgetViewModel {
        val settingsRepo = AndroidSettingsRepository(context)
        val calendarRepo = AndroidCalendarRepository(context)
        val groupUseCase = GroupEventsByDayUseCase()
        val deviceTz = TimeZone.currentSystemDefault()

        val settings: WidgetSettings = settingsRepo.getSettings()
        val cache = calendarRepo.getCachedEvents()
        val dayGroups: List<DayGroup> = groupUseCase(
            events = cache.events,
            settings = settings,
            referenceDate = Clock.System.now(),
            deviceTimezone = deviceTz
        )
        val resolvedMode = when (settings.themeMode) {
            nl.ashhasstudio.kalenda.domain.ThemeMode.SYSTEM -> {
                val night = (context.resources.configuration.uiMode and
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                    android.content.res.Configuration.UI_MODE_NIGHT_YES
                if (night) nl.ashhasstudio.kalenda.domain.ThemeMode.DARK
                else nl.ashhasstudio.kalenda.domain.ThemeMode.LIGHT
            }
            else -> settings.themeMode
        }
        val widgetTheme = themeFor(resolvedMode).copy(accent = accentFor(settings.accentHue))
        return WidgetViewModel(dayGroups, deviceTz, widgetTheme)
    }

    private data class WidgetViewModel(
        val dayGroups: List<DayGroup>,
        val deviceTz: TimeZone,
        val theme: nl.ashhasstudio.kalenda.widget.ui.WidgetTheme,
    )
}
