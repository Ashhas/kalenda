package nl.kabisa.kalenda.usecase

import nl.kabisa.kalenda.data.CalendarRepository
import nl.kabisa.kalenda.data.SettingsRepository
import nl.kabisa.kalenda.domain.CalendarEvent

class FetchEventsUseCase(
    private val calendarRepository: CalendarRepository,
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(): List<CalendarEvent> {
        val settings = settingsRepository.getSettings()
        val allEvents = settings.accounts.flatMap { account ->
            calendarRepository.fetchEvents(account, settings.scrollDays)
        }
        calendarRepository.updateCache(allEvents)
        return allEvents
    }
}
