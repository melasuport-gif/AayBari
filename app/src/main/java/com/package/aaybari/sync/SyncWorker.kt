package com.package.aaybari.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Worker that processes queued items and pushes them to Firestore.
 * - Respects authenticated user (retries if not logged in)
 * - Tracks attempts and moves to FAILED after max attempts
 */
class SyncWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    private val db = SyncDatabase.getInstance(appContext)
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        private const val MAX_ATTEMPTS = 5
    }

    override suspend fun doWork(): Result {
        // Ensure user logged in
        val current = auth.currentUser
        if (current == null) {
            // No user logged in — retry later
            return Result.retry()
        }

        val pending = db.queueDao().getPending("PENDING", limit = 50)
        if (pending.isEmpty()) return Result.success()

        for (entry in pending) {
            try {
                // mark in-progress
                val inProg = entry.copy(status = QueueStatus.IN_PROGRESS.name)
                db.queueDao().update(inProg)

                val payload = JsonUtil.fromJsonToMap(entry.payloadJson)
                // add metadata
                val docRef = firestore.collection(entry.collection).document(entry.docId)
                // Use merge to avoid overwriting other fields
                val withMeta = payload.toMutableMap().apply {
                    put("updatedAt", com.google.firebase.firestore.FieldValue.serverTimestamp())
                    put("updatedBy", current.uid)
                }
                docRef.set(withMeta).await()

                // remove from queue on success
                db.queueDao().deleteById(entry.id)

            } catch (e: Exception) {
                // increment attempts and decide whether to retry or mark failed
                val attempts = entry.attempts + 1
                val newStatus = if (attempts >= MAX_ATTEMPTS) QueueStatus.FAILED.name else QueueStatus.PENDING.name
                val failed = entry.copy(status = newStatus, attempts = attempts, lastError = e.message ?: "")
                db.queueDao().update(failed)

                // If we've exceeded attempts, don't retry the worker (move on). Otherwise ask WorkManager to retry later.
                return if (attempts >= MAX_ATTEMPTS) Result.success() else Result.retry()
            }
        }

        return Result.success()
    }
}
