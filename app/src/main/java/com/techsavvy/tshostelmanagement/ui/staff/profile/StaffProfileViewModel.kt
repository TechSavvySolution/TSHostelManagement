package com.techsavvy.tshostelmanagement.ui.staff.profile

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.techsavvy.tshostelmanagement.data.models.User
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import com.techsavvy.tshostelmanagement.data.utils.CdnUploadService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: FirestoreRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var userData by mutableStateOf<User?>(null)
        private set

    var isEditMode by mutableStateOf(false)
        private set

    var editName by mutableStateOf("")
    var editPhone by mutableStateOf("")

    private val _isUploading = MutableStateFlow(false)
    val isUploading = _isUploading.asStateFlow()

    private val _uploadError = MutableStateFlow<String?>(null)
    val uploadError = _uploadError.asStateFlow()

    init {
        fetchProfile()
    }

    private fun fetchProfile() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val user = repository.getUser(uid)
            userData = user
            editName = user?.name ?: ""
            editPhone = user?.phone ?: ""
        }
    }

    fun toggleEditMode() {
        isEditMode = !isEditMode
        if (isEditMode) {
            editName = userData?.name ?: ""
            editPhone = userData?.phone ?: ""
        }
    }

    fun saveProfile() {
        val uid = auth.currentUser?.uid ?: return
        val current = userData ?: return
        viewModelScope.launch {
            val updated = current.copy(name = editName.trim(), phone = editPhone.trim())
            repository.updateUser(updated)
            userData = updated
            isEditMode = false
        }
    }

    fun pickAndUploadPhoto(uri: Uri) {
        val uid = auth.currentUser?.uid ?: return
        _uploadError.value = null
        viewModelScope.launch {
            _isUploading.value = true
            try {
                val url = CdnUploadService.uploadFile(context, uri)
                repository.updateProfilePhoto(uid, url)
                userData = userData?.copy(profilePhotoUrl = url)
            } catch (e: Exception) {
                _uploadError.value = "Photo upload failed: ${e.message}"
            } finally {
                _isUploading.value = false
            }
        }
    }

    fun clearUploadError() { _uploadError.value = null }
}
