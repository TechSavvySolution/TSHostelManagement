package com.techsavvy.tshostelmanagement.ui.admin.complaints

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techsavvy.tshostelmanagement.data.models.Complaint
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.Flow // Explicit import
import kotlinx.coroutines.flow.SharingStarted // Explicit import
import kotlinx.coroutines.flow.stateIn // Explicit import
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

    fun assignStaff(complaintId: String, staffName: String, staffPhone: String) {
        viewModelScope.launch {
            repository.assignStaff(complaintId, staffName, staffPhone)
        }
    }
}

// Fixed Extension function
fun <T> Flow<T>.toCustomStateFlow(scope: kotlinx.coroutines.CoroutineScope, initialValue: T): StateFlow<T> =
    this.stateIn(scope, SharingStarted.WhileSubscribed(5000), initialValue)