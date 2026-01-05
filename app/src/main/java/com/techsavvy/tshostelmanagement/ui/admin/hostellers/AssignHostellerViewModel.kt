package com.techsavvy.tshostelmanagement.ui.admin.hostellers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techsavvy.tshostelmanagement.data.models.Block
import com.techsavvy.tshostelmanagement.data.models.Floor
import com.techsavvy.tshostelmanagement.data.models.HostellerRoom
import com.techsavvy.tshostelmanagement.data.models.Room
import com.techsavvy.tshostelmanagement.data.models.User
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignHostellerViewModel @Inject constructor(
    private val repository: FirestoreRepository
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
        repository.getUsers().onEach { _users.value = it }.launchIn(viewModelScope)
    }

    private fun fetchBlocks() {
        repository.getBlocks().onEach { _blocks.value = it }.launchIn(viewModelScope)
    }

    fun fetchFloors(blockId: String) {
        repository.getFloors(blockId).onEach { _floors.value = it }.launchIn(viewModelScope)
    }

    fun fetchRooms(floorId: String) {
        repository.getRooms(floorId).onEach { _rooms.value = it }.launchIn(viewModelScope)
    }

    fun assignHosteller(userId: String, roomId: String, notes: String) {
        viewModelScope.launch {
            val assignment = HostellerRoom(
                uid = userId,
                roomId = roomId,
                notes = notes
            )
            repository.assignHostellerRoom(assignment)
        }
    }
}
