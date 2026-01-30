package com.techsavvy.tshostelmanagement.ui.hosteler.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.techsavvy.tshostelmanagement.data.models.Announcement
import com.techsavvy.tshostelmanagement.data.models.User
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HostelerViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    // Announcement State
    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements = _announcements.asStateFlow()

    // Dashboard Properties
    val currentDate: String = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(Date())

    // In a real app, these could be fetched from repository
    val feesStatus = "Paid"

    val messMenu = mapOf(
        "Breakfast" to "Aloo Paratha & Curd",
        "Lunch" to "Rice, Dal, Mixed Veg, Chapati",
        "Dinner" to "Fried Rice & Manchurian"
    )

    init {
        fetchCurrentUser()
        fetchActiveAnnouncements()
    }

    private fun fetchCurrentUser() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            viewModelScope.launch {
                try {
                    val user = repository.getUser(uid)
                    _currentUser.value = user
                } catch (e: Exception) {
                    // Handle error if needed
                }
            }
        }
    }

    /**
     * Fetches only active announcements for the hosteler home screen
     * Sorted by your repository's logic (Order and CreatedAt)
     */
    private fun fetchActiveAnnouncements() {
        viewModelScope.launch {
            try {
                // Using the repository method to collect active announcements
                repository.getAnnouncements(onlyActive = true).collect { list ->
                    _announcements.value = list
                }
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
}