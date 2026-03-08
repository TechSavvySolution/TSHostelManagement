package com.techsavvy.tshostelmanagement.ui.staff.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.techsavvy.tshostelmanagement.data.models.StaffTask
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffHomeViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<StaffTask>>(emptyList())
    val tasks = _tasks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchTasks()
    }

    private fun fetchTasks() {
        val uid = auth.currentUser?.uid ?: return
        _isLoading.value = true
        repository.getStaffTasks(uid).onEach { taskList ->
            // Sort by descending assigned time so newest are at the top
            _tasks.value = taskList.sortedByDescending { it.assignedAt }
            _isLoading.value = false
        }.launchIn(viewModelScope)
    }

    fun updateTaskStatus(taskId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                repository.updateTaskStatus(taskId, newStatus)
            } catch (e: Exception) {
                // Ignore error for now
            }
        }
    }
}
