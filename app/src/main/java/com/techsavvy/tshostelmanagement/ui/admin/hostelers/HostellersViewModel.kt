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

    init {
        fetchHostellers()
        fetchAssignments()
    }

    private fun fetchHostellers() {
        repo.getHostelers().onEach { _allHostellers.value = it }.launchIn(viewModelScope)
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

    // SOFT DELETE — marks the hosteler as deleted without physically removing the document
    fun softDeleteHosteler(uid: String) {
        viewModelScope.launch {
            repo.softDeleteUser(uid)
        }
    }
}