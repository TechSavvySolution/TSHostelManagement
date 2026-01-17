package com.techsavvy.tshostelmanagement.ui.hosteler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.techsavvy.tshostelmanagement.data.models.Complaint
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ComplaintViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _complaints = MutableStateFlow<List<Complaint>>(emptyList())
    val complaints = _complaints.asStateFlow()

    init {
        fetchMyComplaints()
    }

    private fun fetchMyComplaints() {
        val uid = auth.currentUser?.uid ?: return
        repository.getHostelerComplaints(uid).onEach {
            _complaints.value = it.sortedByDescending { c -> c.createdAt }
        }.launchIn(viewModelScope)
    }

    fun submitComplaint(subject: String, message: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            // Fetch latest user info from Firestore to get Email and Phone
            val userProfile = repository.getUser(uid)

            val complaint = Complaint(
                userId = uid,
                userName = userProfile?.name ?: "Hosteller",
                userEmail = userProfile?.email ?: "",
                userPhone = userProfile?.phone ?: "",
                title = subject, // Mapped to the Admin UI 'title' field
                subject = subject,
                message = message,
                status = "Pending"
            )
            repository.saveComplaint(complaint)
        }
    }
}