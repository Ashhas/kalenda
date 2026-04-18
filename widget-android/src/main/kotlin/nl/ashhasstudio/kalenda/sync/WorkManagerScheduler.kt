package nl.ashhasstudio.kalenda.sync

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object WorkManagerScheduler {

    private const val PERIODIC_WORK_TAG = "kalenda_periodic_sync"
    private const val IMMEDIATE_WORK_TAG = "kalenda_immediate_sync"

    fun schedulePeriodicSync(context: Context, clientId: String? = null) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = clientId?.let {
            workDataOf(CalendarSyncWorker.KEY_CLIENT_ID to it)
        } ?: Data.EMPTY

        val periodicRequest = PeriodicWorkRequestBuilder<CalendarSyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setInputData(inputData)
            .addTag(PERIODIC_WORK_TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_WORK_TAG,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicRequest
        )
    }

    fun scheduleImmediateSync(context: Context, clientId: String? = null) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val inputData = clientId?.let {
            workDataOf(CalendarSyncWorker.KEY_CLIENT_ID to it)
        } ?: Data.EMPTY

        val immediateRequest = OneTimeWorkRequestBuilder<CalendarSyncWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .addTag(IMMEDIATE_WORK_TAG)
            .build()

        WorkManager.getInstance(context).enqueue(immediateRequest)
    }
}
