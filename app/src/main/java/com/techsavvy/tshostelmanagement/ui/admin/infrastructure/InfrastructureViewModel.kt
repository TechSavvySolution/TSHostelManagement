package com.techsavvy.tshostelmanagement.ui.admin.infrastructure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.techsavvy.tshostelmanagement.data.models.Block
import com.techsavvy.tshostelmanagement.data.models.Floor
import com.techsavvy.tshostelmanagement.data.models.HostellerRoom
import com.techsavvy.tshostelmanagement.data.models.Room
import com.techsavvy.tshostelmanagement.data.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class InfrastructureViewModel @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _blocks = MutableStateFlow<List<Block>>(emptyList())
    val blocks = _blocks.asStateFlow()

    private val _floors = MutableStateFlow<List<Floor>>(emptyList())
    val floors = _floors.asStateFlow()

    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms = _rooms.asStateFlow()

    private val _selectedBlock = MutableStateFlow<Block?>(null)
    val selectedBlock = _selectedBlock.asStateFlow()

    private val _selectedFloor = MutableStateFlow<Floor?>(null)
    val selectedFloor = _selectedFloor.asStateFlow()

    private val _selectedRoom = MutableStateFlow<Room?>(null)
    val selectedRoom = _selectedRoom.asStateFlow()

    // Students in each context
    private val _studentsInBlock = MutableStateFlow<List<User>>(emptyList())
    val studentsInBlock = _studentsInBlock.asStateFlow()

    private val _studentsInFloor = MutableStateFlow<List<User>>(emptyList())
    val studentsInFloor = _studentsInFloor.asStateFlow()

    private val _studentsInRoom = MutableStateFlow<List<User>>(emptyList())
    val studentsInRoom = _studentsInRoom.asStateFlow()

    private val _snackbarChannel = Channel<String>()
    val snackbarFlow = _snackbarChannel.receiveAsFlow()

    init {
        getBlocks()
        getFloors()
        getRooms()
    }

    fun getBlock(id: String) {
        db.collection("blocks").document(id).addSnapshotListener { snapshot, _ ->
            _selectedBlock.value = snapshot?.toObject<Block>()
        }
    }

    fun getFloor(id: String) {
        db.collection("floors").document(id).addSnapshotListener { snapshot, _ ->
            _selectedFloor.value = snapshot?.toObject<Floor>()
        }
    }

    fun getRoom(id: String) {
        db.collection("rooms").document(id).addSnapshotListener { snapshot, _ ->
            _selectedRoom.value = snapshot?.toObject<Room>()
        }
    }

    private fun getBlocks() {
        db.collection("blocks").whereEqualTo("deleted", false).addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                _blocks.value = snapshot.documents.mapNotNull { it.toObject<Block>() }
            }
        }
    }

    fun addBlock(name: String, alias: String?) {
        if (_blocks.value.any { it.name.equals(name, ignoreCase = true) }) {
            _snackbarChannel.trySend("A block with this name already exists")
            return
        }
        val block = Block(name = name, alias = alias)
        db.collection("blocks").add(block).addOnSuccessListener { _snackbarChannel.trySend("Block added successfully") }
    }

    fun updateBlock(block: Block) {
        db.collection("blocks").document(block.id).set(block).addOnSuccessListener { _snackbarChannel.trySend("Block updated successfully") }
    }

    fun deleteBlock(id: String) {
        db.collection("blocks").document(id)
            .update("deleted", true)
            .addOnSuccessListener {
                _snackbarChannel.trySend("Block removed successfully")
                _floors.value.filter { it.blockId == id }.forEach { floor ->
                    deleteFloorSilently(floor.id)
                }
            }
    }

    private fun getFloors() {
        db.collection("floors")
            .whereEqualTo("deleted", false)
            .addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                _floors.value = snapshot.documents.mapNotNull { it.toObject<Floor>() }
            }
        }
    }

    fun getFloorsForBlock(blockId: String) {
        db.collection("floors").whereEqualTo("blockId", blockId)
            .whereEqualTo("deleted", false)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    _floors.value = snapshot.documents.mapNotNull { it.toObject<Floor>() }
                }
            }
    }

    fun addFloor(name: String, blockId: String, alias: String? = null) {
        if (_floors.value.any { it.blockId == blockId && it.name.equals(name, ignoreCase = true) }) {
            _snackbarChannel.trySend("A floor with this name already exists in this block")
            return
        }
        val floor = Floor(name = name, blockId = blockId, alias = alias)
        db.collection("floors").add(floor).addOnSuccessListener { _snackbarChannel.trySend("Floor added successfully") }
    }

    fun updateFloor(floor: Floor) {
        db.collection("floors").document(floor.id).set(floor).addOnSuccessListener { _snackbarChannel.trySend("Floor updated successfully") }
    }

    fun deleteFloor(id: String) {
        db.collection("floors").document(id)
            .update("deleted", true)
            .addOnSuccessListener {
                _snackbarChannel.trySend("Floor removed successfully")
                deleteFloorSilently(id)
            }
    }

    private fun deleteFloorSilently(id: String) {
        db.collection("floors").document(id).update("deleted", true)
        _rooms.value.filter { it.floorId == id }.forEach { room ->
            db.collection("rooms").document(room.id).update("deleted", true)
        }
    }

    private fun getRooms() {
        db.collection("rooms")
            .whereEqualTo("deleted", false)
            .addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                _rooms.value = snapshot.documents.mapNotNull { it.toObject<Room>() }
            }
        }
    }

    fun getRoomsForFloor(floorId: String) {
        db.collection("rooms").whereEqualTo("floorId", floorId)
            .whereEqualTo("deleted", false)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    _rooms.value = snapshot.documents.mapNotNull { it.toObject<Room>() }
                }
            }
    }

    fun addRoom(name: String, roomNumber: Int, floorId: String, blockId: String, capacity: Int) {
        if (_rooms.value.any { it.floorId == floorId && it.roomNumber == roomNumber }) {
            _snackbarChannel.trySend("A room with this number already exists on this floor")
            return
        }
        val room = Room(
            name = name,
            roomNumber = roomNumber,
            floorId = floorId,
            blockId = blockId,
            capacity = capacity
        )
        db.collection("rooms").add(room).addOnSuccessListener { _snackbarChannel.trySend("Room added successfully") }
    }

    fun updateRoom(room: Room) {
        db.collection("rooms").document(room.id).set(room).addOnSuccessListener { _snackbarChannel.trySend("Room updated successfully") }
    }

    fun deleteRoom(id: String) {
        db.collection("rooms").document(id)
            .update("deleted", true)
            .addOnSuccessListener { _snackbarChannel.trySend("Room removed successfully") }
    }

    fun deleteItem(type: String, id: String) {
        when (type.lowercase()) {
            "block" -> deleteBlock(id)
            "floor" -> deleteFloor(id)
            "room" -> deleteRoom(id)
            else -> {
                db.collection("${type}s").document(id)
                    .update("deleted", true)
                    .addOnSuccessListener { _snackbarChannel.trySend("$type removed successfully") }
            }
        }
    }

    // ─── Student Fetch Methods ────────────────────────────────────────────────

    fun fetchStudentsForRoom(roomId: String) {
        viewModelScope.launch {
            _studentsInRoom.value = getUsersForRoom(roomId)
        }
    }

    fun fetchStudentsForFloor(floorId: String) {
        viewModelScope.launch {
            try {
                val roomIds = db.collection("rooms")
                    .whereEqualTo("floorId", floorId)
                    .whereEqualTo("deleted", false)
                    .get().await()
                    .documents.mapNotNull { it.id }
                val users = mutableListOf<User>()
                roomIds.forEach { rid -> users.addAll(getUsersForRoom(rid)) }
                _studentsInFloor.value = users.distinctBy { it.uid }
            } catch (e: Exception) {
                e.printStackTrace()
                _studentsInFloor.value = emptyList()
            }
        }
    }

    fun fetchStudentsForBlock(blockId: String) {
        viewModelScope.launch {
            try {
                val roomIds = db.collection("rooms")
                    .whereEqualTo("blockId", blockId)
                    .whereEqualTo("deleted", false)
                    .get().await()
                    .documents.mapNotNull { it.id }
                val users = mutableListOf<User>()
                roomIds.forEach { rid -> users.addAll(getUsersForRoom(rid)) }
                _studentsInBlock.value = users.distinctBy { it.uid }
            } catch (e: Exception) {
                e.printStackTrace()
                _studentsInBlock.value = emptyList()
            }
        }
    }

    private suspend fun getUsersForRoom(roomId: String): List<User> {
        return try {
            val assignments = db.collection("hosteller_room")
                .whereEqualTo("roomId", roomId)
                .get().await()
                .toObjects(HostellerRoom::class.java)
            assignments.mapNotNull { assignment ->
                val doc = db.collection("users").document(assignment.uid).get().await()
                val user = doc.toObject<User>()
                if (user?.deleted != true) user else null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}