package com.techsavvy.tshostelmanagement.ui.admin.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techsavvy.tshostelmanagement.data.models.StaffTask
import com.techsavvy.tshostelmanagement.data.models.User
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignTaskViewModel @Inject constructor(
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _staffMembers = MutableStateFlow<List<User>>(emptyList())
    val staffMembers = _staffMembers.asStateFlow()

    private val _assignSuccess = MutableStateFlow(false)
    val assignSuccess = _assignSuccess.asStateFlow()

    init {
        fetchStaff()
    }

    private fun fetchStaff() {
        repository.getStaff().onEach { _staffMembers.value = it }.launchIn(viewModelScope)
    }

    fun assignTask(staff: User, title: String, description: String) {
        viewModelScope.launch {
            val task = StaffTask(
                staffUid = staff.uid,
                staffName = staff.name,
                taskTitle = title,
                description = description,
                status = "Pending",
                assignedAt = System.currentTimeMillis()
            )
            repository.assignStaffTask(task)
            _assignSuccess.value = true
        }
    }
}