package com.techsavvy.tshostelmanagement.ui.admin.staff

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.techsavvy.tshostelmanagement.data.models.User
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import com.techsavvy.tshostelmanagement.data.utils.Role
import com.techsavvy.tshostelmanagement.ui.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AddStaffViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: FirestoreRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState = _authState.asStateFlow()

    fun registerStaff(email: String, password: String, username: String, phone: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            val secondaryAppName = "StaffReg_${System.currentTimeMillis()}"
            val options = auth.app.options
            
            try {
                // Use secondary app to avoid logging out the current Admin
                val secondaryApp = FirebaseApp.initializeApp(context, options, secondaryAppName)
                val secondaryAuth = FirebaseAuth.getInstance(secondaryApp)

                val result = secondaryAuth.createUserWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid

                if (uid != null) {
                    val user = User(
                        uid = uid,
                        name = username,
                        email = email,
                        phone = phone,
                        role = Role.STAFF,
                        active = true
                    )
                    repository.saveUser(user)
                    
                    secondaryApp.delete()
                    _authState.value = AuthState.RegistrationSuccess
                } else {
                    _authState.value = AuthState.Error("Staff creation failed.")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Registration failed.")
            }
        }
    }
}
