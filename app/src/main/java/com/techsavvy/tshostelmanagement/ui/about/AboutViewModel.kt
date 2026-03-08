package com.techsavvy.tshostelmanagement.ui.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techsavvy.tshostelmanagement.data.models.Developer
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _developers = MutableStateFlow<List<Developer>>(emptyList())
    val developers: StateFlow<List<Developer>> = _developers.asStateFlow()

    init {
        fetchDevelopers()
    }

    private fun fetchDevelopers() {
        viewModelScope.launch {
            repository.getDevelopers().collect {
                _developers.value = it
            }
        }
    }
}
