package com.techsavvy.tshostelmanagement.ui.admin.hostellers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.techsavvy.tshostelmanagement.data.models.Block
import com.techsavvy.tshostelmanagement.data.models.Floor
import com.techsavvy.tshostelmanagement.data.models.Room
import com.techsavvy.tshostelmanagement.data.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignHostellerViewModel @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    private val _blocks = MutableStateFlow<List<Block>>(emptyList())
    val blocks = _blocks.asStateFlow()

    private val _floors = MutableStateFlow<List<Floor>>(emptyList())
    val floors = _floors.asStateFlow()

    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms = _rooms.asStateFlow()

    init {
        fetchUsers()
        fetchBlocks()
    }

    private fun fetchUsers() {
        db.collection("users").whereEqualTo("role", "hosteller")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let { _users.value = it.toObjects() }
            }
    }

    private fun fetchBlocks() {
        db.collection("blocks")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let { _blocks.value = it.toObjects() }
            }
    }

    fun fetchFloors(blockId: String) {
        db.collection("floors").whereEqualTo("blockId", blockId)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let { _floors.value = it.toObjects() }
            }
    }

    fun fetchRooms(floorId: String) {
        db.collection("rooms").whereEqualTo("floorId", floorId)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let { _rooms.value = it.toObjects() }
            }
    }

    fun assignHosteller(userId: String, roomId: String, notes: String) {
        val assignment = hashMapOf(
            "userId" to userId,
            "roomId" to roomId,
            "notes" to notes,
            "assignedAt" to System.currentTimeMillis()
        )
        db.collection("hosteller_room").add(assignment)
    }
}
