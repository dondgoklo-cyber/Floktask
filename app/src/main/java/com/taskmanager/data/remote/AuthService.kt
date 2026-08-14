package com.taskmanager.data.remote

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthService @Inject constructor(
    private val firebaseService: FirebaseService
) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val currentUserUid: String? get() = firebaseService.getCurrentUserId()

    suspend fun signInWithEmail(email: String, password: String): AuthResult =
        auth.signInWithEmailAndPassword(email, password).await()

    suspend fun signUpWithEmail(email: String, password: String): AuthResult =
        auth.createUserWithEmailAndPassword(email, password).await()

    fun signOut() {
        auth.signOut()
    }
}
