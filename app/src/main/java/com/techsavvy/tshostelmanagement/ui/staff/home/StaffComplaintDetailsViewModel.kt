package com.techsavvy.tshostelmanagement.ui.staff.complaints

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techsavvy.tshostelmanagement.data.models.Complaint
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffComplaintDetailsViewModel @Inject constructor(
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _complaint = MutableStateFlow<Complaint?>(null)
    val complaint = _complaint.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchComplaintDetails(complaintId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _complaint.value = repository.getComplaintById(complaintId)
            _isLoading.value = false
        }
    }

    fun updateStatus(complaintId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateComplaintStatus(complaintId, newStatus)
            // Refresh local data after update
            fetchComplaintDetails(complaintId)
        }
    }
}