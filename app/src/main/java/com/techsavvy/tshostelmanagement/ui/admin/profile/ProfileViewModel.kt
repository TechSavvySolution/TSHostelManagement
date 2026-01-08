package com.techsavvy.tshostelmanagement.ui.admin.profile

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    var name by mutableStateOf("Admin User")
        private set

    var isEditMode by mutableStateOf(false)
        private set

    // State for the profile image URI
    var selectedImageUri by mutableStateOf<Uri?>(null)
        private set

    fun onNameChange(newName: String) {
        name = newName
    }

    fun onImageSelected(uri: Uri?) {
        selectedImageUri = uri
    }

    fun toggleEditMode() {
        isEditMode = !isEditMode
    }

    fun saveProfile() {
        isEditMode = false
    }
}