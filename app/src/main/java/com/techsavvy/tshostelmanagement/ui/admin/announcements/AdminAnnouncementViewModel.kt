package com.techsavvy.tshostelmanagement.ui.admin.announcements

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techsavvy.tshostelmanagement.data.models.Announcement
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminAnnouncementViewModel @Inject constructor(
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements = _announcements.asStateFlow()

    // State for Add/Edit
    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var imageUrl by mutableStateOf("")
    var order by mutableStateOf("0")
    var isActive by mutableStateOf(true)

    init {
        fetchAnnouncements()
    }

    private fun fetchAnnouncements() {
        viewModelScope.launch {
            repository.getAnnouncements(onlyActive = false).collect { list ->
                // Sort by 'order' in memory to avoid the need for a composite index
                _announcements.value = list.sortedBy { it.order }
            }
        }
    }

    fun deleteAnnouncement(id: String) = viewModelScope.launch {
        repository.deleteAnnouncement(id)
    }

    fun loadAnnouncement(id: String) = viewModelScope.launch {
        val current = _announcements.value.find { it.id == id }
        current?.let {
            title = it.title
            description = it.description
            imageUrl = it.imageUrl ?: ""
            order = it.order.toString()
            isActive = it.isActive
        }
    }

    fun saveAnnouncement(id: String? = null, onSuccess: () -> Unit) = viewModelScope.launch {
        val announcement = Announcement(
            id = id ?: "",
            title = title,
            description = description,
            imageUrl = imageUrl.ifBlank { null },
            order = order.toIntOrNull() ?: 0,
            isActive = isActive,
            createdAt = System.currentTimeMillis()
        )
        repository.saveAnnouncement(announcement)
        onSuccess()
    }
}