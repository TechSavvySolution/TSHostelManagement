package com.techsavvy.tshostelmanagement.ui.admin.infrastructure

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.techsavvy.tshostelmanagement.ui.admin.infrastructure.HostelViewModel

@Composable
fun EditBlockScreen(
    navController: NavController,
    viewModel: HostelViewModel = hiltViewModel(),
    blockId: String?
) {
    LaunchedEffect(blockId) {
        blockId?.let { viewModel.getBlock(it) }
    }

    val block by viewModel.selectedBlock.collectAsState()
    var blockName by remember(block) { mutableStateOf(block?.name ?: "") }
    var blockAlias by remember(block) { mutableStateOf(block?.alias ?: "") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF010413)
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp)) {
            Text("Edit Block", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(value = blockName, onValueChange = { blockName = it }, label = { Text("Block Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = blockAlias, onValueChange = { blockAlias = it }, label = { Text("Block Alias (Optional)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {
                block?.let { currentBlock ->
                    viewModel.updateBlock(currentBlock.copy(name = blockName, alias = blockAlias))
                    navController.popBackStack()
                }
            }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ADE80))) {
                Text("Save Changes", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun EditFloorScreen(
    navController: NavController,
    viewModel: HostelViewModel = hiltViewModel(),
    floorId: String?
) {
    LaunchedEffect(floorId) {
        floorId?.let { viewModel.getFloor(it) }
    }

    val floor by viewModel.selectedFloor.collectAsState()
    var floorName by remember(floor) { mutableStateOf(floor?.name ?: "") }
    var floorNumber by remember(floor) { mutableStateOf(floor?.
    floorNumber?.toString() ?: "") }
    var description by remember(floor) { mutableStateOf(floor?.description ?: "") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF010413)
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp)) {
            Text("Edit Floor", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(value = floorName, onValueChange = { floorName = it }, label = { Text("Floor Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = floorNumber, onValueChange = { floorNumber = it }, label = { Text("Floor Number") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {
                floor?.let { currentFloor ->
                    viewModel.updateFloor(currentFloor.copy(name = floorName, floorNumber = floorNumber.toIntOrNull() ?: 0, description = description))
                    navController.popBackStack()
                }
            }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ADE80))) {
                Text("Save Changes", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun EditRoomScreen(
    navController: NavController,
    viewModel: HostelViewModel = hiltViewModel(),
    roomId: String?
) {
    LaunchedEffect(roomId) {
        roomId?.let { viewModel.getRoom(it) }
    }

    val room by viewModel.selectedRoom.collectAsState()
    var roomName by remember(room) { mutableStateOf(room?.name ?: "") }
    var roomNumber by remember(room) { mutableStateOf(room?.roomNumber?.toString() ?: "") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF010413)
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp)) {
            Text("Edit Room", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(value = roomName, onValueChange = { roomName = it }, label = { Text("Room Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = roomNumber, onValueChange = { roomNumber = it }, label = { Text("Room Number") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {
                room?.let { currentRoom ->
                    viewModel.updateRoom(currentRoom.copy(name = roomName, roomNumber = roomNumber.toIntOrNull() ?: 0))
                    navController.popBackStack()
                }
            }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ADE80))) {
                Text("Save Changes", fontSize = 18.sp)
            }
        }
    }
}
