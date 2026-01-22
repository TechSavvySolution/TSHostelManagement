package com.techsavvy.tshostelmanagement.ui.admin.complaints

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techsavvy.tshostelmanagement.data.models.Complaint
import com.techsavvy.tshostelmanagement.data.models.User
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminComplaintViewModel @Inject constructor(
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _complaints = MutableStateFlow<List<Complaint>>(emptyList())
    val complaints = _complaints.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val staffList: StateFlow<List<User>> = repository.getStaff()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var isStaffDropdownExpanded by mutableStateOf(false)

    val filteredComplaints = combine(_complaints, _searchQuery) { list, query ->
        if (query.isEmpty()) list
        else {
            list.filter {
                it.userName.contains(query, ignoreCase = true) ||
                        it.title.contains(query, ignoreCase = true) ||
                        it.roomNo.contains(query)
            }
        }
    }.toCustomStateFlow(viewModelScope, emptyList())

    init {
        observeComplaints()
    }

    private fun observeComplaints() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllComplaints().collect {
                _complaints.value = it
                _isLoading.value = false
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun updateStatus(complaintId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateComplaintStatus(complaintId, newStatus)
        }
    }

    fun assignStaff(complaintId: String, staffUid: String, staffName: String, staffPhone: String) {
        viewModelScope.launch {
            repository.assignStaff(complaintId, staffUid, staffName, staffPhone)
        }
    }

    fun deleteComplaint(complaintId: String) {
        viewModelScope.launch {
            repository.deleteComplaint(complaintId)
        }
    }
}

fun <T> Flow<T>.toCustomStateFlow(scope: kotlinx.coroutines.CoroutineScope, initialValue: T): StateFlow<T> =
    this.stateIn(scope, SharingStarted.WhileSubscribed(5000), initialValue)