package com.techsavvy.tshostelmanagement.ui.admin.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techsavvy.tshostelmanagement.data.models.User
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user
    
    // UI state for error or success
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage

    fun loadUser(uid: String) {
        viewModelScope.launch {
            val u = repository.getUser(uid)
            _user.value = u
        }
    }

    fun updateUser(updatedUser: User) {
        viewModelScope.launch {
            try {
                repository.updateUser(updatedUser)
                _statusMessage.value = "Profile updated successfully"
            } catch (e: Exception) {
                _statusMessage.value = "Failed to update profile: ${e.message}"
            }
        }
    }
    
    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
