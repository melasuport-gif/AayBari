# Sync (DataStore/Room ↔ Firestore)

This feature branch implements a local-first sync queue using Room + WorkManager to push local changes to Cloud Firestore, and a realtime listener to apply remote changes locally.

How it works (short):
- UI writes to local DB/DataStore and then calls SyncManager.enqueueWrite(collection, docId, payload).
- SyncManager stores a queue row (Room) and schedules a SyncWorker via WorkManager.
- SyncWorker processes queued items and writes them to Firestore using document.set(..., merge).
- FirestoreRealtimeListener listens to remote changes and the app should update local DB accordingly.

Changes & hardening in this update
- Added attempt tracking and lastError columns to QueueEntity and a Room migration (v1→v2).
- SyncWorker now increments attempts and marks items FAILED after MAX_ATTEMPTS (default 5).
- QueueDao orders pending items by attempts then created_at so fresher, low-attempt items run first.
- SyncManager exposes reenqueueFailed(id) to reset attempts and reschedule sync.

Important notes before testing locally:
1. Place your google-services.json in app/ (do NOT commit it publicly).
2. The worker uses FirebaseAuth.currentUser — ensure a user is signed in when testing sync.
3. This is a starter implementation; map payload fields to your existing DataStore models in Converters and listeners.
4. If you change the DB schema further, increment SyncDatabase.version and add a migration.

Testing tips
- Create a queue item via SyncManager.enqueueWrite(...), then observe WorkManager logs and Firestore console for document writes.
- To simulate failures, try enqueueing a write while offline — worker will retry/backoff.
- To move an item to retry manually, use SyncManager.reenqueueFailed(id).
