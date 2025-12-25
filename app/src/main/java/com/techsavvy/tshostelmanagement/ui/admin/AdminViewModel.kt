package com.techsavvy.tshostelmanagement.ui.admin

import androidx.lifecycle.ViewModel
import com.techsavvy.tshostelmanagement.data.models.Building
import com.techsavvy.tshostelmanagement.data.models.Floor
import com.techsavvy.tshostelmanagement.data.models.Room
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AdminUiState(
    val buildings: List<Building> = emptyList(),
    val showAddBuildingDialog: Boolean = false,
    val showAddFloorDialog: Boolean = false,
    val showAddRoomDialog: Boolean = false,
    val selectedBuildingId: String? = null,
    val selectedFloorId: String? = null,
    val expandedBuildingIds: Set<String> = emptySet(),
    val expandedFloorIds: Set<String> = emptySet()
)

class AdminViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    val totalBuildings: StateFlow<Int> = MutableStateFlow(0)
    val totalFloors: StateFlow<Int> = MutableStateFlow(0)
    val totalRooms: StateFlow<Int> = MutableStateFlow(0)
    val totalHostellers: StateFlow<Int> = MutableStateFlow(0)

    fun showDialog(dialog: String, show: Boolean) {
        when (dialog) {
            "building" -> _uiState.value = _uiState.value.copy(showAddBuildingDialog = show)
            "floor" -> _uiState.value = _uiState.value.copy(showAddFloorDialog = show)
            "room" -> _uiState.value = _uiState.value.copy(showAddRoomDialog = show)
        }
    }

    fun selectBuilding(buildingId: String) {
        _uiState.value = _uiState.value.copy(selectedBuildingId = buildingId)
    }

    fun selectFloor(floorId: String) {
        _uiState.value = _uiState.value.copy(selectedFloorId = floorId)
    }

    fun toggleBuildingExpansion(buildingId: String) {
        val expandedIds = _uiState.value.expandedBuildingIds.toMutableSet()
        if (expandedIds.contains(buildingId)) {
            expandedIds.remove(buildingId)
        } else {
            expandedIds.add(buildingId)
        }
        _uiState.value = _uiState.value.copy(expandedBuildingIds = expandedIds)
    }

    fun toggleFloorExpansion(floorId: String) {
        val expandedIds = _uiState.value.expandedFloorIds.toMutableSet()
        if (expandedIds.contains(floorId)) {
            expandedIds.remove(floorId)
        } else {
            expandedIds.add(floorId)
        }
        _uiState.value = _uiState.value.copy(expandedFloorIds = expandedIds)
    }

    fun onAddBuilding(name: String, description: String?) {
        // Add building logic here
        showDialog("building", false)
    }

    fun onAddFloor(number: String) {
        // Add floor logic here
        showDialog("floor", false)
    }

    fun onAddRoom(number: String, capacity: Int) {
        // Add room logic here
        showDialog("room", false)
    }
}
