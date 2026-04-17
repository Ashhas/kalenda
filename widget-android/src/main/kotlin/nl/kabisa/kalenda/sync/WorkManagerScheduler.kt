package nl.kabisa.kalenda.sync

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object WorkManagerScheduler {

    private const val PERIODIC_WORK_TAG = "kalenda_periodic_sync"
    private const val IMMEDIATE_WORK_TAG = "kalenda_immediate_sync"

    fun schedulePeriodicSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicRequest = PeriodicWorkRequestBuilder<CalendarSyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .addTag(PERIODIC_WORK_TAG)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_WORK_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }

    fun scheduleImmediateSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val immediateRequest = OneTimeWorkRequestBuilder<CalendarSyncWorker>()
            .setConstraints(constraints)
            .addTag(IMMEDIATE_WORK_TAG)
            .build()

        WorkManager.getInstance(context).enqueue(immediateRequest)
    }
}
