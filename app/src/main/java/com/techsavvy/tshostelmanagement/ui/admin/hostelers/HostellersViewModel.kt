package com.techsavvy.tshostelmanagement.ui.admin.hostelers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.techsavvy.tshostelmanagement.data.models.User
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HostellersViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val repo: FirestoreRepository
) : ViewModel() {

    private val _allHostellers = MutableStateFlow<List<User>>(emptyList())
    private val _assignedUserIds = MutableStateFlow<Set<String>>(emptySet())

    val allHostellers: StateFlow<List<User>> = _allHostellers
    val assignedHostellers: Flow<List<User>> = repo.getAssignedUsers()

    // Map of uid -> Triple(roomName, floorName, blockName)
    private val _roomInfoMap = MutableStateFlow<Map<String, Triple<String, String, String>>>(emptyMap())
    val roomInfoMap: StateFlow<Map<String, Triple<String, String, String>>> = _roomInfoMap

    init {
        fetchHostellers()
        fetchAssignments()
    }

    private fun fetchHostellers() {
        repo.getHostelers().onEach { users ->
            _allHostellers.value = users
            // Batch-fetch room info for each hosteler
            fetchRoomInfoForAll(users)
        }.launchIn(viewModelScope)
    }

    private fun fetchRoomInfoForAll(users: List<User>) {
        viewModelScope.launch {
            val map = mutableMapOf<String, Triple<String, String, String>>()
            users.forEach { user ->
                val info = try {
                    repo.getHostelerRoomInfo(user.uid)
                } catch (_: Exception) { null }
                if (info != null) {
                    map[user.uid] = info
                }
            }
            _roomInfoMap.value = map
        }
    }

    private fun fetchAssignments() {
        db.collection("hosteller_rooms").addSnapshotListener { snapshot, error ->
            if (error != null) { error.printStackTrace(); return@addSnapshotListener }
            if (snapshot != null) {
                val assignedIds = snapshot.documents.mapNotNull { doc ->
                    doc.getString("uid") ?: doc.getString("userId") ?: doc.getString("hostellerId")
                }.toSet()
                _assignedUserIds.value = assignedIds
            }
        }
    }

    fun softDeleteHosteler(uid: String) {
        viewModelScope.launch {
            repo.softDeleteUser(uid)
        }
    }
}