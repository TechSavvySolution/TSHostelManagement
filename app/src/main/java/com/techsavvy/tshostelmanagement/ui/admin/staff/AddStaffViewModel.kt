package com.techsavvy.tshostelmanagement.ui.admin.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.techsavvy.tshostelmanagement.data.models.User
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import com.techsavvy.tshostelmanagement.data.utils.Role
import com.techsavvy.tshostelmanagement.ui.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AddStaffViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState = _authState.asStateFlow()

    fun registerStaff(email: String, password: String, username: String, phone: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // 1. Get default app options
                val defaultApp = FirebaseApp.getInstance()
                val options = defaultApp.options
                
                // 2. Initialize a secondary app manually
                val secondaryAppName = "SecondaryApp_${System.currentTimeMillis()}"
                val secondaryApp = FirebaseApp.initializeApp(defaultApp.applicationContext, options, secondaryAppName)
                
                // 3. Get Auth instance for the secondary app
                val secondaryAuth = FirebaseAuth.getInstance(secondaryApp)
                
                // 4. Create the user on the secondary instance (doesn't affect primary auth state)
                val result = secondaryAuth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user

                if (firebaseUser != null) {
                    val user = User(
                        uid = firebaseUser.uid,
                        name = username,
                        email = email,
                        pass = password,
                        phone = phone,
                        role = Role.STAFF,
                        active = true
                    )
                    // 2. Wait for Firestore to save before updating state
                    repository.saveUser(user)

                    // 3. Success
                    _authState.value = AuthState.Authenticated(user)
                } else {
                    _authState.value = AuthState.Error("User creation failed.")
                }
                
                // Clean up: Delete the secondary app
                secondaryApp.delete()
                
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Registration failed.")
            }
        }
    }
}