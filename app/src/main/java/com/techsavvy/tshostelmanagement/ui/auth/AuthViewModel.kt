package com.techsavvy.tshostelmanagement.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.techsavvy.tshostelmanagement.data.models.User
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import com.techsavvy.tshostelmanagement.data.utils.Role
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState = _authState.asStateFlow()
    private val _isLoading = MutableStateFlow<Boolean>(false);
    val isLoading = _isLoading


    init {
        checkUserExists()
    }

    fun checkUserExists(){
        viewModelScope.launch {
            _isLoading.value = true
            val uid = auth.uid
            if (uid != null) {
                val user = repository.getUser(uid)
                if (user != null) {
                    _authState.value = AuthState.Authenticated(user)
                } else {
                    _authState.value = AuthState.Error("User data not found.")
                }
            } else {
                _authState.value = AuthState.Error("Authentication failed: User not found.")
            }
            _isLoading.value = false
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
                } else {
                    _authState.value = AuthState.Error("Authentication failed: User not found.")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Authentication failed.")
            }
        }
    }

    fun registerUser(email: String, password: String, username: String, phone: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user
                firebaseUser?.let {
                    val user = User(
                        uid = it.uid,
                        name = username,
                        email = email,
                        pass = password,
                        phone = phone,
                        role = Role.HOSTELER,
                        active = true
                    )
                    repository.saveUser(user)
                    _authState.value = AuthState.Authenticated(user)
                } ?: run {
                    _authState.value = AuthState.Error("Registration failed: User could not be created.")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration failed.")
            }
        }
    }

    // Creates a user without kicking the current Admin out by using a secondary Firebase app instance
    fun adminRegisterUser(email: String, password: String, username: String, phone: String, role: Role = Role.HOSTELER) {
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
                    // 5. Save user data to Firestore (using primary app's repository)
                    val user = User(
                        uid = firebaseUser.uid,
                        name = username,
                        email = email,
                        pass = password,
                        phone = phone,
                        role = role,
                        active = true
                    )
                    repository.saveUser(user)
                    // Briefly flash authenticated to let the UI pop the backstack
                    _authState.value = AuthState.Authenticated(user)
                } else {
                    _authState.value = AuthState.Error("Admin Registration failed: User could not be created.")
                }
                
                // 6. Clean up: Delete the secondary app
                secondaryApp.delete()

            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Admin Registration failed.")
            }
        }
    }
}
