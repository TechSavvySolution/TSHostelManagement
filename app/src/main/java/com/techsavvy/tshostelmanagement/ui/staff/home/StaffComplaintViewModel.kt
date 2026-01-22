package com.techsavvy.tshostelmanagement.ui.staff.complaints

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.techsavvy.tshostelmanagement.data.models.Complaint
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffComplaintViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _complaints = MutableStateFlow<List<Complaint>>(emptyList())
    val complaints = _complaints.asStateFlow()

    init {
        fetchAssignedComplaints()
    }

    private fun fetchAssignedComplaints() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            // Note: Ensure this method name matches your FirestoreRepository
            repository.getAssignedStaffComplaints(uid).collect {
                _complaints.value = it
            }
        }
    }

    // Renamed from updateComplaintStatus to updateStatus to match UI call
    fun updateStatus(id: String, status: String) {
        viewModelScope.launch {
            repository.updateComplaintStatus(id, status)
        }
    }
}