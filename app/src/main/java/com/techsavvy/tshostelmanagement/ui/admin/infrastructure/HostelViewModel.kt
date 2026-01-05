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
class HostelViewModel @Inject constructor(
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
        db.collection("blocks").addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                _blocks.value = snapshot.documents.mapNotNull { it.toObject<Block>() }
            }
        }
    }

    fun addBlock(name: String, alias: String?) {
        val block = Block(name = name, alias = alias)
        db.collection("blocks").add(block).addOnSuccessListener { _snackbarChannel.trySend("Block added successfully") }
    }

    fun updateBlock(block: Block) {
        db.collection("blocks").document(block.id).set(block).addOnSuccessListener { _snackbarChannel.trySend("Block updated successfully") }
    }

    fun deleteBlock(id: String) {
        db.collection("blocks").document(id).delete().addOnSuccessListener { _snackbarChannel.trySend("Block deleted successfully") }
    }

    private fun getFloors() {
        db.collection("floors").addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                _floors.value = snapshot.documents.mapNotNull { it.toObject<Floor>() }
            }
        }
    }

    fun getFloorsForBlock(blockId: String) {
        db.collection("floors").whereEqualTo("blockId", blockId)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    _floors.value = snapshot.documents.mapNotNull { it.toObject<Floor>() }
                }
            }
    }

    fun addFloor(name: String, blockId: String, alias: String? = null) {
        val floor = Floor(name = name, blockId = blockId, alias = alias)
        db.collection("floors").add(floor).addOnSuccessListener { _snackbarChannel.trySend("Floor added successfully") }
    }

    fun updateFloor(floor: Floor) {
        db.collection("floors").document(floor.id).set(floor).addOnSuccessListener { _snackbarChannel.trySend("Floor updated successfully") }
    }

    fun deleteFloor(id: String) {
        db.collection("floors").document(id).delete().addOnSuccessListener { _snackbarChannel.trySend("Floor deleted successfully") }
    }

    private fun getRooms() {
        db.collection("rooms").addSnapshotListener { snapshot, e ->
            if (snapshot != null) {
                _rooms.value = snapshot.documents.mapNotNull { it.toObject<Room>() }
            }
        }
    }

    fun getRoomsForFloor(floorId: String) {
        db.collection("rooms").whereEqualTo("floorId", floorId)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    _rooms.value = snapshot.documents.mapNotNull { it.toObject<Room>() }
                }
            }
    }

    fun addRoom(name: String, roomNumber: Int, floorId: String, blockId: String, capacity: Int) {
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
        db.collection("rooms").document(id).delete().addOnSuccessListener { _snackbarChannel.trySend("Room deleted successfully") }
    }

    fun deleteItem(type: String, id: String) {
        db.collection("${type}s").document(id).delete().addOnSuccessListener { _snackbarChannel.trySend("$type deleted successfully") }
    }
}