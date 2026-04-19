package nl.ashhasstudio.kalenda.widget

import android.content.Context
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
import nl.ashhasstudio.kalenda.usecase.GroupEventsByDayUseCase
import nl.ashhasstudio.kalenda.widget.ui.WidgetContent
import nl.ashhasstudio.kalenda.widget.ui.themeFor

class KalendaWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val settingsRepo = AndroidSettingsRepository(context)
        val calendarRepo = AndroidCalendarRepository(context)
        val groupUseCase = GroupEventsByDayUseCase()
        val deviceTz = TimeZone.currentSystemDefault()

        val settings = settingsRepo.getSettings()
        val cache = calendarRepo.getCachedEvents()
        val dayGroups: List<DayGroup> = groupUseCase(
            events = cache.events,
            settings = settings,
            referenceDate = Clock.System.now(),
            deviceTimezone = deviceTz
        )
        val widgetTheme = themeFor(settings.themeMode)

        provideContent {
            currentState<Preferences>()

            val size = LocalSize.current
            val isScrollable = size.height.value > 200f
            WidgetContent(
                dayGroups = dayGroups,
                deviceTimezone = deviceTz,
                isScrollable = isScrollable,
                theme = widgetTheme,
            )
        }
    }
}
