package com.techsavvy.tshostelmanagement.ui.auth

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.techsavvy.tshostelmanagement.data.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {
    val currentUser = auth.currentUser

    private var _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading

    private var _user = MutableStateFlow<User?>(null)
    val user = _user

    fun login(email: String, password: String) {
        _isLoading.value = true

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val uid = it.result.user?.uid

                    db.collection("users")
                        .document(uid!!)
                        .get()
                        .addOnCompleteListener {
                            _isLoading.value = false
                            if (it.isSuccessful) {
                                _user.value = it.result.toObject<User>()
                                Log.d("USER VALUE",user.value.toString())
                            } else {
                                it.exception?.printStackTrace()
                            }
                        }
                } else {
                    _isLoading.value = false
                    it.exception?.printStackTrace()
                }
            }
            .addOnFailureListener {
                _isLoading.value = false
                it.printStackTrace()
            }
    }
}