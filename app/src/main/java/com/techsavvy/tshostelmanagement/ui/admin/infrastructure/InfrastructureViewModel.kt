package com.techsavvy.tshostelmanagement.ui.admin.infrastructure

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.techsavvy.tshostelmanagement.data.models.Block
import com.techsavvy.tshostelmanagement.data.models.Floor
import com.techsavvy.tshostelmanagement.data.models.Room
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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

    // SOFT DELETE — sets deleted=true on the block document
    fun deleteBlock(id: String) {
        db.collection("blocks").document(id)
            .update("deleted", true)
            .addOnSuccessListener { 
                _snackbarChannel.trySend("Block removed successfully") 
                // Cascade delete associated floors and rooms
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

    // SOFT DELETE — sets deleted=true on the floor document
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

    // SOFT DELETE — sets deleted=true on the room document
    fun deleteRoom(id: String) {
        db.collection("rooms").document(id)
            .update("deleted", true)
            .addOnSuccessListener { _snackbarChannel.trySend("Room removed successfully") }
    }

    // Generic soft delete used for cascade operations (when deleting a block with children)
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
}