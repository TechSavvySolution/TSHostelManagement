package com.techsavvy.tshostelmanagement.ui.admin.hostelers

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.techsavvy.tshostelmanagement.data.models.User
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HostellersViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val repo: FirestoreRepository
) : ViewModel() {

    // Internal mutable state
    private val _allHostellers = MutableStateFlow<List<User>>(emptyList())
    private val _assignedUserIds = MutableStateFlow<Set<String>>(emptySet())

    // 1. ALL USERS (Both Assigned and Unassigned)
    // Exposed to the UI for the "All User" tab
    val allHostellers: StateFlow<List<User>> = _allHostellers

    // 2. ASSIGNED USERS ONLY
    // Filters the allHostellers list to include only those whose UIDs are found in the assignments
    val assignedHostellers: Flow<List<User>> = repo.getAssignedUsers()

    init {
        fetchHostellers()
        fetchAssignments()
    }

    private fun fetchHostellers() {
        repo.getHostelers().onEach { _allHostellers.value = it }.launchIn(viewModelScope)
        // Listen to 'users' collection for real-time updates
//        db.collection("users").addSnapshotListener { snapshot, error ->
//            if (error != null) {
//                error.printStackTrace()
//                return@addSnapshotListener
//            }
//            if (snapshot != null) {
//                val userList = snapshot.documents.mapNotNull { doc ->
//                    val user = doc.toObject(User::class.java)
//                    // Manually set UID from document ID to ensure it's accurate
//                    user?.copy(uid = doc.id)
//                }
//                // Filter only hostellers (Ignore case prevents issues with "Hosteller" vs "hosteller")
//                _allHostellers.value = userList.filter {
//                    it.role.equals("hosteller", ignoreCase = true)
//                }
//            }
//        }
    }

    private fun fetchAssignments() {
        // Listen to 'hosteller_rooms' to track who has a room.
        // This collection name matches the standard data model for HostellerRoom.
        db.collection("hosteller_rooms").addSnapshotListener { snapshot, error ->
            if (error != null) {
                error.printStackTrace()
                return@addSnapshotListener
            }
            if (snapshot != null) {
                // We extract the 'uid' field from the assignment documents.
                // We check multiple possible field names to be safe.
                val assignedIds = snapshot.documents.mapNotNull { doc ->
                    doc.getString("uid") ?: doc.getString("userId") ?: doc.getString("hostellerId")
                }.toSet()

                _assignedUserIds.value = assignedIds
            }
        }
    }
}