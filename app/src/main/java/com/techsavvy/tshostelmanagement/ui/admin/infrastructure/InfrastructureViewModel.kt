
package com.techsavvy.tshostelmanagement.ui.admin.infrastructure

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject

// Data models
data class Block(val id: String, val name: String, val alias: String?)
data class Floor(val id: String, val number: String, val blockName: String)
data class Room(val id: String, val number: String, val capacity: String, val floor: String, val blockName: String)

// UI State
data class InfrastructureUiState(
    val blocks: List<Block> = emptyList(),
    val floors: List<Floor> = emptyList(),
    val rooms: List<Room> = emptyList(),
    val isLoading: Boolean = false
) {
    val totalBlocks: Int get() = blocks.size
    val totalFloors: Int get() = floors.size
    val totalRooms: Int get() = rooms.size
    val totalCapacity: Int get() = rooms.sumOf { it.capacity.toIntOrNull() ?: 0 }

    fun getRoomCountForBlock(blockName: String): Int = rooms.count { it.blockName == blockName }
    fun getFloorCountForBlock(blockName: String): Int = floors.count { it.blockName == blockName }
}

@HiltViewModel
class InfrastructureViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(InfrastructureUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSampleData()
    }

    fun addBlock(name: String, alias: String?) {
        val newBlock = Block(id = UUID.randomUUID().toString(), name = name, alias = alias)
        _uiState.update { it.copy(blocks = it.blocks + newBlock) }
    }

    fun addFloor(number: String, blockName: String) {
        val newFloor = Floor(id = UUID.randomUUID().toString(), number = number, blockName = blockName)
        _uiState.update { it.copy(floors = it.floors + newFloor) }
    }

    fun addRoom(number: String, capacity: String, floor: String, blockName: String) {
        val newRoom = Room(id = UUID.randomUUID().toString(), number = number, capacity = capacity, floor = floor, blockName = blockName)
        _uiState.update { it.copy(rooms = it.rooms + newRoom) }
    }

    fun updateBlock(id: String, name: String, alias: String?) {
        _uiState.update { state ->
            val updatedBlocks = state.blocks.map {
                if (it.id == id) it.copy(name = name, alias = alias) else it
            }
            state.copy(blocks = updatedBlocks)
        }
    }

    fun updateFloor(id: String, number: String, blockName: String) {
        _uiState.update { state ->
            val updatedFloors = state.floors.map {
                if (it.id == id) it.copy(number = number, blockName = blockName) else it
            }
            state.copy(floors = updatedFloors)
        }
    }

    fun updateRoom(id: String, number: String, capacity: String, floor: String, blockName: String) {
        _uiState.update { state ->
            val updatedRooms = state.rooms.map {
                if (it.id == id) it.copy(number = number, capacity = capacity, floor = floor, blockName = blockName) else it
            }
            state.copy(rooms = updatedRooms)
        }
    }

    fun deleteBlock(blockId: String) {
        _uiState.update { state ->
            val blockToDelete = state.blocks.find { it.id == blockId }
            state.copy(
                blocks = state.blocks.filterNot { it.id == blockId },
                floors = state.floors.filterNot { it.blockName == blockToDelete?.name },
                rooms = state.rooms.filterNot { it.blockName == blockToDelete?.name }
            )
        }
    }

    fun deleteFloor(floorId: String) {
        _uiState.update { state ->
            state.copy(floors = state.floors.filterNot { it.id == floorId })
        }
    }

    fun deleteRoom(roomId: String) {
        _uiState.update { state ->
            state.copy(rooms = state.rooms.filterNot { it.id == roomId })
        }
    }

    private fun loadSampleData() {
        _uiState.update { it.copy(isLoading = true) }
        val sampleBlocks = listOf(
            Block(id = "1", name = "Block A", alias = "A"),
            Block(id = "2", name = "Block B", alias = "B"),
            Block(id = "3", name = "Ladies Block", alias = "C")
        )
        val sampleFloors = listOf(
            Floor(id = "f1", number = "1", blockName = "Block A"),
            Floor(id = "f2", number = "2", blockName = "Block A"),
            Floor(id = "f3", number = "1", blockName = "Block B"),
        )
        val sampleRooms = listOf(
            Room(id = "r1", number = "101", capacity = "4", floor = "1", blockName = "Block A"),
            Room(id = "r2", number = "102", capacity = "4", floor = "1", blockName = "Block A"),
            Room(id = "r3", number = "201", capacity = "2", floor = "2", blockName = "Block A"),
            Room(id = "r4", number = "101", capacity = "3", floor = "1", blockName = "Block B"),
        )

        _uiState.update {
            it.copy(
                blocks = sampleBlocks,
                floors = sampleFloors,
                rooms = sampleRooms,
                isLoading = false
            )
        }
    }
}
