package com.package.aaybari.sync

import android.content.Context
import androidx.work.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * High-level SyncManager API. UI or Repository should call enqueueWrite(...) after
 * writing locally to DataStore/Room. SyncManager stores a queue entry and schedules
 * a WorkManager job to push changes to Firestore.
 */
class SyncManager(private val context: Context, private val db: SyncDatabase) {

    private val workManager = WorkManager.getInstance(context)

    suspend fun enqueueWrite(collection: String, docId: String, payload: Map<String, Any?>) {
        // insert queue entry
        val entry = QueueEntity(
            id = 0,
            collection = collection,
            docId = docId,
            payloadJson = JsonUtil.toJson(payload),
            status = QueueStatus.PENDING.name,
            createdAt = System.currentTimeMillis()
        )
        db.queueDao().insert(entry)
        scheduleSync()
    }

    fun scheduleSync() {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()
        workManager.enqueueUniqueWork("aaybari_sync", ExistingWorkPolicy.KEEP, request)
    }

}
