AayBari — Firebase Auth & Firestore starter

This branch adds starter files to integrate Firebase Authentication and Cloud Firestore with role-based access for AayBari. It includes a minimal AuthManager (Kotlin), Firestore security rules, Gradle config notes, and setup instructions.

Files added in branch feature/firebase-auth-firestore:
- app/build.gradle (module) - Firebase and Android dependencies
- app/src/main/AndroidManifest.xml - INTERNET permission
- app/src/main/java/com/package/aaybari/auth/AuthManager.kt - signUp/signIn sample (Kotlin)
- firestore.rules - role-based security rules (primary/admin/viewer)

Important setup steps (do these on your machine / Firebase console):

1) Create a Firebase project
   - Go to https://console.firebase.google.com and create a new Project (e.g., AayBari-prod or AayBari-dev)

2) Enable Email/Password sign-in
   - Authentication → Sign-in method → Enable Email/Password

3) Add Android app to Firebase
   - Project settings → Add app → Android
   - Package name: com.package.aaybari
   - Provide SHA-1 fingerprint(s) for debug & release (see below)
   - Download google-services.json and place it into the repo at app/google-services.json (DO NOT COMMIT secrets to public repo)

4) Firestore database
   - Firestore Database → Create database → Start in production rules mode (you can use test for dev but be cautious)
   - After creating DB, deploy the rules in firestore.rules (see below) via Firebase Console or CLI

5) Deploy rules (recommended via Firebase CLI)
   - Install Firebase CLI: https://firebase.google.com/docs/cli
   - firebase login
   - firebase init firestore (choose project and rules file)
   - copy firestore.rules content to the configured rules file and deploy: firebase deploy --only firestore:rules

6) Create initial Primary Admin (development step)
   Option A (Console - manual):
     - Authentication → Users → Add user
     - Email: admin@aaybari.local  Password: admin123  (DEV only — change in production)
     - Note the UID of this user and create a document in Firestore at /admins/{UID} with fields:
         {
           uid: "{UID}",
           displayName: "Primary Admin",
           email: "admin@aaybari.local",
           role: "primary",
           createdAt: <timestamp>
         }

   Option B (App - first run flow):
     - Implement a small onboarding flow to create the first primary admin if /admins collection is empty. (Not included in this starter.)

7) Local development: google-services.json
   - Place the downloaded google-services.json in app/ directory. Do NOT commit it to public repos. Use GitHub secrets or CI variables for CI builds.

8) SHA-1 fingerprints
   - Debug SHA-1: keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
   - Release SHA-1: keytool -list -v -keystore your_release_keystore.jks -alias your_alias

9) After you set up Firebase and google-services.json, build the app in Android Studio and test sign-up / sign-in using the AuthManager sample.

Notes & next steps
- This starter does NOT include DataStore sync logic, Drive backup, or PDF generation. After Auth+Firestore is verified, I will implement the DataStore ↔ Firestore SyncQueue, Google Drive backup flow, and PDF generation (A4 ledger & custom bill) as you requested.
- If you want, I can create the Primary Admin automatically on first run (ask me to add the onboarding code).

If you want me to continue, I will now push the actual Kotlin integration into your app, and then create a PR with instructions on how to test. Reply: "Continue and create PR" and I will proceed.
