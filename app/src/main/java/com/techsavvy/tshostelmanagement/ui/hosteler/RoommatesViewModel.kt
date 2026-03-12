package com.techsavvy.tshostelmanagement.ui.hosteler

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.techsavvy.tshostelmanagement.data.models.User
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class RoommatesViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: FirestoreRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _roommates = MutableStateFlow<List<User>>(emptyList())
    val roommates = _roommates.asStateFlow()

    private val _roomName = MutableStateFlow("")
    val roomName = _roomName.asStateFlow()

    private val _floorName = MutableStateFlow("")
    val floorName = _floorName.asStateFlow()

    private val _blockName = MutableStateFlow("")
    val blockName = _blockName.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val selfUid = auth.currentUser?.uid ?: ""

    init {
        fetchRoommates()
    }

    private fun fetchRoommates() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Resolve room / floor / block names
                val info = repository.getHostelerRoomInfo(uid)
                if (info != null) {
                    _roomName.value = info.first
                    _floorName.value = info.second
                    _blockName.value = info.third
                }

                // 2. Get raw roomId for this hosteler
                val roomId = firestore.collection("hosteller_room")
                    .whereEqualTo("uid", uid)
                    .limit(1)
                    .get().await()
                    .documents
                    .firstOrNull()
                    ?.getString("roomId")

                // 3. Fetch all occupants, filter out self
                if (roomId != null) {
                    val all = repository.getRoommatesForRoom(roomId)
                    _roommates.value = all.filter { it.uid != selfUid }
                }
            } catch (_: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }
}
