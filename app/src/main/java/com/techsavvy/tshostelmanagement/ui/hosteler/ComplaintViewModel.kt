package com.techsavvy.tshostelmanagement.ui.hosteler

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.techsavvy.tshostelmanagement.data.models.Complaint
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import com.techsavvy.tshostelmanagement.data.utils.CdnUploadService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComplaintViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: FirestoreRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _complaints = MutableStateFlow<List<Complaint>>(emptyList())
    val complaints = _complaints.asStateFlow()

    // Upload state exposed to the UI
    private val _isUploading = MutableStateFlow(false)
    val isUploading = _isUploading.asStateFlow()

    private val _uploadError = MutableStateFlow<String?>(null)
    val uploadError = _uploadError.asStateFlow()

    init {
        fetchMyComplaints()
    }

    private fun fetchMyComplaints() {
        val uid = auth.currentUser?.uid ?: return
        repository.getHostelerComplaints(uid).onEach {
            _complaints.value = it.sortedByDescending { c -> c.createdAt }
        }.launchIn(viewModelScope)
    }

    /**
     * Submits a complaint, optionally uploading any attached media files to the CDN first.
     *
     * @param subject   The complaint subject / title
     * @param message   The detailed complaint message
     * @param mediaUris Optional list of local content URIs (images or video) to attach
     */
    fun submitComplaint(
        subject: String,
        message: String,
        mediaUris: List<Uri> = emptyList(),
        navController: NavController
    ) {
        val uid = auth.currentUser?.uid ?: return
        _uploadError.value = null

        viewModelScope.launch {
            _isUploading.value = true
            try {
                // 1. Upload media files to CDN (empty list → skipped)
                val uploadedUrls: List<String> = if (mediaUris.isNotEmpty()) {
                    CdnUploadService.uploadFiles(context, mediaUris)
                } else {
                    emptyList()
                }
                println(mediaUris.toString())


                // 2. Fetch latest user profile for contact details
                val userProfile = repository.getUser(uid)

                // 3. Build and save the complaint
                val complaint = Complaint(
                    userId = uid,
                    userName = userProfile?.name ?: "Hosteller",
                    userEmail = userProfile?.email ?: "",
                    userPhone = userProfile?.phone ?: "",
                    profilePhotoUrl = userProfile?.profilePhotoUrl ?: "",
                    title = subject,
                    subject = subject,
                    message = message,
                    status = "Pending",
                    mediaUrls = uploadedUrls
                )
                repository.saveComplaint(complaint)

            } catch (e: Exception) {
                _uploadError.value = "Upload failed: ${e.message}"
            } finally {
                _isUploading.value = false
                navController.popBackStack()
            }
        }
    }

    fun clearUploadError() {
        _uploadError.value = null
    }

    // Soft-delete complaint (hosteler side)
    fun deleteComplaint(complaintId: String) {
        viewModelScope.launch {
            repository.deleteComplaint(complaintId)
        }
    }
}