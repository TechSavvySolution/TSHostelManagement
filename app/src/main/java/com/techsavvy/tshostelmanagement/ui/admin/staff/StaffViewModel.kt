package com.techsavvy.tshostelmanagement.ui.admin.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techsavvy.tshostelmanagement.data.models.User
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class StaffViewModel @Inject constructor(
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _staffList = MutableStateFlow<List<User>>(emptyList())
    val staffList = _staffList.asStateFlow()

    init {
        fetchStaff()
    }

    private fun fetchStaff() {
        // Uses the getStaff() method added to your repository
        repository.getStaff().onEach {
            _staffList.value = it
        }.launchIn(viewModelScope)
    }
}