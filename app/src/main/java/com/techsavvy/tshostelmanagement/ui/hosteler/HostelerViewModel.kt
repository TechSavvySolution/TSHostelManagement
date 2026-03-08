package com.techsavvy.tshostelmanagement.ui.hosteler.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.techsavvy.tshostelmanagement.data.models.Announcement
import com.techsavvy.tshostelmanagement.data.models.MessMenu
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

    // Mess Menu State (live from Firestore)
    private val _messMenu = MutableStateFlow(MessMenu())
    val messMenu = _messMenu.asStateFlow()

    // Dashboard Properties
    val currentDate: String = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(Date())

    // feesStatus will be updated by HostelerFeesViewModel on home screen
    val feesStatus = "Unpaid" // default — replaced by real data in home screen

    // Room Info: Triple(RoomName, FloorName, BlockName)
    private val _roomInfo = MutableStateFlow<Triple<String, String, String>?>(null)
    val roomInfo = _roomInfo.asStateFlow()

    init {
        fetchCurrentUser()
        fetchActiveAnnouncements()
        fetchMessMenu()
    }

    private fun fetchCurrentUser() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            viewModelScope.launch {
                try {
                    val user = repository.getUser(uid)
                    _currentUser.value = user
                    _roomInfo.value = repository.getHostelerRoomInfo(uid)
                } catch (e: Exception) {
                    // Handle error if needed
                }
            }
        }
    }

    /**
     * Fetches only active announcements for the hosteler home screen
     */
    private fun fetchActiveAnnouncements() {
        viewModelScope.launch {
            try {
                repository.getAnnouncements(onlyActive = true).collect { list ->
                    _announcements.value = list
                }
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    private fun fetchMessMenu() {
        viewModelScope.launch {
            try {
                repository.getMessMenu().collect { menu ->
                    _messMenu.value = menu
                }
            } catch (e: Exception) {
                // Fallback silently
            }
        }
    }
}