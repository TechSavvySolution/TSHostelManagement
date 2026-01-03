package com.techsavvy.tshostelmanagement.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.techsavvy.tshostelmanagement.data.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterUserViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    var loading by mutableStateOf(false)
        private set

    var successUser by mutableStateOf<User?>(null)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun registerUser(email: String, password: String, username: String, phone: String) {
        loading = true
        error = null
        viewModelScope.launch {
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
                                    loading = false
                                    successUser = user
                                }
                                .addOnFailureListener { e ->
                                    loading = false
                                    error = e.message ?: "Something went wrong"
                                }
                        }
                    } else {
                        loading = false
                        error = task.exception?.message ?: "Authentication failed"
                    }
                }
        }
    }
}
