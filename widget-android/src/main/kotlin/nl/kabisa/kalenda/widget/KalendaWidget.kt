package nl.kabisa.kalenda.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import nl.kabisa.kalenda.data.AndroidCalendarRepository
import nl.kabisa.kalenda.data.AndroidSettingsRepository
import nl.kabisa.kalenda.usecase.GroupEventsByDayUseCase
import nl.kabisa.kalenda.widget.ui.WidgetContent

class KalendaWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val settingsRepo = AndroidSettingsRepository(context)
        val calendarRepo = AndroidCalendarRepository(context)
        val groupUseCase = GroupEventsByDayUseCase()

        val settings = settingsRepo.getSettings()
        val cache = calendarRepo.getCachedEvents()
        val deviceTz = TimeZone.currentSystemDefault()
        val dayGroups = groupUseCase(
            events = cache.events,
            settings = settings,
            referenceDate = Clock.System.now(),
            deviceTimezone = deviceTz
        )

        provideContent {
            val size = LocalSize.current
            val isScrollable = size.height.value > 200f
            WidgetContent(
                dayGroups = dayGroups,
                deviceTimezone = deviceTz,
                isScrollable = isScrollable
            )
        }
    }
}
