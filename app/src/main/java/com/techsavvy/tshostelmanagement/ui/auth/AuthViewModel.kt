package com.techsavvy.tshostelmanagement.ui.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.techsavvy.tshostelmanagement.data.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState = _authState.asStateFlow()

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result.user?.uid
                    if (uid != null) {
                        db.collection("users")
                            .document(uid)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document != null && document.exists()) {
                                    val user = document.toObject(User::class.java)
                                    _authState.value = AuthState.Authenticated(user)
                                } else {
                                    _authState.value = AuthState.Error("User data not found in Firestore.")
                                }
                            }
                            .addOnFailureListener { exception ->
                                _authState.value = AuthState.Error(exception.message ?: "Failed to fetch user data.")
                            }
                    } else {
                        _authState.value = AuthState.Error("Authentication failed: User not found.")
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Authentication failed.")
                }
            }
    }

    fun registerUser(email: String, password: String, username: String, phone: String) {
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    firebaseUser?.let {
                        val user = User(
                            uid = it.uid,
                            name = username,
                            email = email,
                            phone = phone,
                            role = "hosteller",
                            active = true
                        )
                        db.collection("users").document(it.uid).set(user)
                            .addOnSuccessListener {
                                _authState.value = AuthState.Authenticated(user)
                            }
                            .addOnFailureListener { e ->
                                _authState.value = AuthState.Error(e.message ?: "Something went wrong")
                            }
                    }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Authentication failed")
                }
            }
    }
}
