package nl.kabisa.kalenda.sync

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import nl.kabisa.kalenda.data.AndroidCalendarRepository
import nl.kabisa.kalenda.data.AndroidSettingsRepository
import nl.kabisa.kalenda.usecase.FetchEventsUseCase
import nl.kabisa.kalenda.widget.KalendaWidget

class CalendarSyncWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val settingsRepo = AndroidSettingsRepository(context)
            val calendarRepo = AndroidCalendarRepository(context)
            val fetchUseCase = FetchEventsUseCase(calendarRepo, settingsRepo)
            fetchUseCase()
            KalendaWidget().updateAll(context)
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
