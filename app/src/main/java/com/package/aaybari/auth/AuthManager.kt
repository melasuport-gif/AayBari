package com.package.aaybari.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Simple AuthManager to create/sign-in users and store role information in Firestore.
 * This is a starting point — adapt error handling and UI integration for your app.
 */

data class AppUser(
    val uid: String = "",
    val displayName: String? = null,
    val email: String? = null,
    val role: String = "viewer", // valid: "primary", "admin", "viewer"
    val createdAt: Long = System.currentTimeMillis()
)

class AuthManager(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun signUpWithEmail(email: String, password: String, displayName: String?, role: String = "viewer"): Result<AppUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("User creation failed")
            val appUser = AppUser(uid = user.uid, displayName = displayName, email = email, role = role)
            db.collection("admins").document(user.uid).set(appUser).await()
            Result.success(appUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<AppUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("Sign-in failed")
            val doc = db.collection("admins").document(user.uid).get().await()
            val appUser = doc.toObject(AppUser::class.java) ?: AppUser(uid = user.uid, email = user.email, role = "viewer")
            Result.success(appUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun currentUser(): FirebaseUser? = auth.currentUser
}
