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
import nl.ashhasstudio.kalenda.usecase.FetchOutcome
import nl.ashhasstudio.kalenda.widget.KalendaWidget

class CalendarSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val settingsRepo = AndroidSettingsRepository(applicationContext)
            val calendarRepo = AndroidCalendarRepository(applicationContext)
            val clientId = inputData.getString(KEY_CLIENT_ID)
            val tokenRefresher = clientId?.let { GoogleTokenRefresher(it) }
            val fetchUseCase = FetchEventsUseCase(calendarRepo, settingsRepo, tokenRefresher)
            val outcomes = fetchUseCase()
            val anySuccess = outcomes.any { it is FetchOutcome.Ok }
            if (outcomes.isNotEmpty() && !anySuccess) {
                Log.w("KalendaSync", "All accounts failed: $outcomes")
                return@doWork if (runAttemptCount < 5) Result.retry() else Result.failure()
            }
            KalendaWidget().updateAll(applicationContext)
            Result.success()
        } catch (e: Exception) {
            Log.e("KalendaSync", "Sync failed (attempt $runAttemptCount)", e)
            if (runAttemptCount < 5) Result.retry() else Result.failure()
        }
    }

    companion object {
        const val KEY_CLIENT_ID = "client_id"
    }
}
