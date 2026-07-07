package com.package.aaybari.sync

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Realtime listener that applies remote changes from Firestore to local storage.
 * This is a minimal example: you should map documents to your local models and
 * write to DataStore/Room accordingly.
 */
class FirestoreRealtimeListener(private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)) {

    private val firestore = FirebaseFirestore.getInstance()
    private val listeners = mutableListOf<ListenerRegistration>()

    fun startListening(collection: String, onDocumentChanged: (DocumentSnapshot) -> Unit) {
        val registration = firestore.collection(collection)
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null) return@addSnapshotListener
                for (dc in snapshots.documentChanges) {
                    // For simplicity call onDocumentChanged for ADDED/MODIFIED
                    if (dc.type != com.google.firebase.firestore.DocumentChange.Type.REMOVED) {
                        scope.launch {
                            onDocumentChanged(dc.document)
                        }
                    }
                }
            }
        listeners.add(registration)
    }

    fun stopAll() {
        listeners.forEach { it.remove() }
        listeners.clear()
    }
}
