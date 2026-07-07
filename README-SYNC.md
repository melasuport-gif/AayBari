# Sync (DataStore/Room ↔ Firestore)

This feature branch implements a local-first sync queue using Room + WorkManager to push local changes to Cloud Firestore, and a realtime listener to apply remote changes locally.

How it works (short):
- UI writes to local DB/DataStore and then calls SyncManager.enqueueWrite(collection, docId, payload).
- SyncManager stores a queue row (Room) and schedules a SyncWorker via WorkManager.
- SyncWorker processes queued items and writes them to Firestore using document.set(..., merge).
- FirestoreRealtimeListener listens to remote changes and the app should update local DB accordingly.

Important notes before testing locally:
1. Place your google-services.json in app/ (do NOT commit it publicly).
2. The worker uses FirebaseAuth.currentUser — ensure a user is signed in when testing sync.
3. This is a starter implementation; map payload fields to your existing DataStore models in Converters and listeners.

Next steps I will do after review:
- Integrate with your existing DataStore models and repositories.
- Add automatic retries, dead-letter queue and monitoring metrics.
- Add sample unit tests and instrumentation tests for the worker.
