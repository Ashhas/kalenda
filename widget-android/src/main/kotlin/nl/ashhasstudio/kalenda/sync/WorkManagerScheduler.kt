package nl.ashhasstudio.kalenda.sync

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

private const val PERIODIC_WORK_TAG = "kalenda_periodic_sync"

fun schedulePeriodicSync(context: Context, clientId: String? = null) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val inputData = clientId?.let {
        workDataOf(CalendarSyncWorker.KEY_CLIENT_ID to it)
    } ?: Data.EMPTY

    val periodicRequest = PeriodicWorkRequestBuilder<CalendarSyncWorker>(15, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
        .setInputData(inputData)
        .addTag(PERIODIC_WORK_TAG)
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        PERIODIC_WORK_TAG,
        ExistingPeriodicWorkPolicy.UPDATE,
        periodicRequest
    )
}
