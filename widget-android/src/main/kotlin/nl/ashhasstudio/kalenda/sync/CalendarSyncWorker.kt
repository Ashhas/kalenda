package nl.ashhasstudio.kalenda.sync

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import nl.ashhasstudio.kalenda.data.AndroidCalendarRepository
import nl.ashhasstudio.kalenda.data.AndroidSettingsRepository
import nl.ashhasstudio.kalenda.data.GoogleTokenRefresher
import nl.ashhasstudio.kalenda.usecase.FetchEventsUseCase
import nl.ashhasstudio.kalenda.widget.KalendaWidget

class CalendarSyncWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val settingsRepo = AndroidSettingsRepository(context)
            val calendarRepo = AndroidCalendarRepository(context)
            val clientId = inputData.getString(KEY_CLIENT_ID)
            val tokenRefresher = clientId?.let { GoogleTokenRefresher(it) }
            val fetchUseCase = FetchEventsUseCase(calendarRepo, settingsRepo, tokenRefresher)
            fetchUseCase()
            KalendaWidget().updateAll(context)
            Result.success()
        } catch (e: Exception) {
            Log.e("Kalenda", "Sync failed (attempt $runAttemptCount)", e)
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    companion object {
        const val KEY_CLIENT_ID = "client_id"
    }
}
