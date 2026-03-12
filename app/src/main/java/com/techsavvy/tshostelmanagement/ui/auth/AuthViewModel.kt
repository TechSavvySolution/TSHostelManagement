package com.techsavvy.tshostelmanagement.ui.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.techsavvy.tshostelmanagement.data.models.User
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import com.techsavvy.tshostelmanagement.data.utils.Role
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: FirestoreRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState = _authState.asStateFlow()

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        checkUserExists()
    }

    private fun checkUserExists() {
        val uid = auth.uid
        if (uid != null) {
            viewModelScope.launch {
                _isLoading.value = true
                val user = repository.getUser(uid)
                if (user != null) {
                    _authState.value = AuthState.Authenticated(user)
                } else {
                    _authState.value = AuthState.Error("User data not found.")
                }
                _isLoading.value = false
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid
                if (uid != null) {
                    val user = repository.getUser(uid)
                    if (user != null) {
                        _authState.value = AuthState.Authenticated(user)
                    } else {
                        _authState.value = AuthState.Error("User data not found.")
                    }
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Authentication failed.")
            }
        }
    }

    /**
     * Professional method to create a user WITHOUT logging out the current Admin.
     * It uses a secondary Firebase instance internally.
     */
    fun adminRegisterUser(
        email: String,
        password: String,
        username: String,
        phone: String,
        role: Role = Role.HOSTELER
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            // Step 1: Initialize a secondary Firebase instance
            val secondaryAppName = "SecondaryApp_${System.currentTimeMillis()}"
            val options = auth.app.options
            
            try {
                val secondaryApp = FirebaseApp.initializeApp(context, options, secondaryAppName)
                val secondaryAuth = FirebaseAuth.getInstance(secondaryApp)

                // Step 2: Create user in the secondary instance (doesn't affect main login)
                val result = secondaryAuth.createUserWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid

                if (uid != null) {
                    val user = User(
                        uid = uid,
                        name = username,
                        email = email,
                        phone = phone,
                        role = role,
                        active = true
                    )
                    
                    // Step 3: Save to Firestore via main repository
                    repository.saveUser(user)
                    
                    // Step 4: Cleanup
                    secondaryApp.delete()
                    _authState.value = AuthState.RegistrationSuccess
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Failed to create user.")
            }
        }
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Initial
    }
}
