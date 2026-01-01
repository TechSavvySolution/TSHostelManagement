package com.techsavvy.tshostelmanagement.data.repositories

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun registerUser(email: String, password: String, onResult: (Result<String>) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = firebaseAuth.currentUser?.uid
                    if (uid != null) {
                        onResult(Result.success(uid))
                    } else {
                        onResult(Result.failure(Exception("Failed to get user id.")))
                    }
                } else {
                    onResult(Result.failure(task.exception ?: Exception("Unknown error occurred.")))
                }
            }
    }
}
