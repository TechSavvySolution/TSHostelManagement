package com.techsavvy.tshostelmanagement.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.techsavvy.tshostelmanagement.models.User
import com.techsavvy.tshostelmanagement.data.repositories.AuthRepository
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterUserViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    var loading by mutableStateOf(false)
        private set

    var successUser by mutableStateOf<User?>(null)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun registerUser(email: String, password: String, name: String, phone: String) {
        loading = true
        error = null
        successUser = null

        authRepository.registerUser(email, password) { result ->
            result.onSuccess { uid ->
                val user = User(
                    uid = uid,
                    name = name,
                    email = email,
                    phone = phone
                )
                firestoreRepository.saveUser(user)
                successUser = user
                loading = false
            }.onFailure { exception ->
                error = exception.message
                loading = false
            }
        }
    }
}
