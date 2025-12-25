package com.techsavvy.tshostelmanagement.ui.admin.infrastructure

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun EditBlockScreen(
    navController: NavController,
    viewModel: InfrastructureViewModel,
    blockId: String?
) {
    val block = viewModel.uiState.collectAsState().value.blocks.find { it.id == blockId }
    var blockName by remember(block) { mutableStateOf(block?.name ?: "") }
    var blockAlias by remember(block) { mutableStateOf(block?.alias ?: "") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF010413)
    ) {
        Column(modifier = Modifier.padding(it).fillMaxSize().padding(16.dp)) {
            Text("Edit Block", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(value = blockName, onValueChange = { blockName = it }, label = { Text("Block Name") }, modifier = Modifier.fillMaxWidth(), colors = getOutlinedTextFieldColors())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = blockAlias, onValueChange = { blockAlias = it }, label = { Text("Block Alias (Optional)") }, modifier = Modifier.fillMaxWidth(), colors = getOutlinedTextFieldColors())
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {
                if (block != null) {
                    viewModel.updateBlock(block.id, blockName, blockAlias)
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
    viewModel: InfrastructureViewModel,
    floorId: String?
) {
    val floor = viewModel.uiState.collectAsState().value.floors.find { it.id == floorId }
    var floorNumber by remember(floor) { mutableStateOf(floor?.number ?: "") }
    var blockName by remember(floor) { mutableStateOf(floor?.blockName ?: "") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF010413)
    ) {
        Column(modifier = Modifier.padding(it).fillMaxSize().padding(16.dp)) {
            Text("Edit Floor", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(value = floorNumber, onValueChange = { floorNumber = it }, label = { Text("Floor Number") }, modifier = Modifier.fillMaxWidth(), colors = getOutlinedTextFieldColors())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = blockName, onValueChange = { blockName = it }, label = { Text("Block Name") }, modifier = Modifier.fillMaxWidth(), colors = getOutlinedTextFieldColors())
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {
                if (floor != null) {
                    viewModel.updateFloor(floor.id, floorNumber, blockName)
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
    viewModel: InfrastructureViewModel,
    roomId: String?
) {
    val room = viewModel.uiState.collectAsState().value.rooms.find { it.id == roomId }
    var roomNumber by remember(room) { mutableStateOf(room?.number ?: "") }
    var capacity by remember(room) { mutableStateOf(room?.capacity ?: "") }
    var floor by remember(room) { mutableStateOf(room?.floor ?: "") }
    var blockName by remember(room) { mutableStateOf(room?.blockName ?: "") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF010413)
    ) {
        Column(modifier = Modifier.padding(it).fillMaxSize().padding(16.dp)) {
            Text("Edit Room", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(value = roomNumber, onValueChange = { roomNumber = it }, label = { Text("Room Number") }, modifier = Modifier.fillMaxWidth(), colors = getOutlinedTextFieldColors())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = capacity, onValueChange = { capacity = it }, label = { Text("Capacity") }, modifier = Modifier.fillMaxWidth(), colors = getOutlinedTextFieldColors())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = floor, onValueChange = { floor = it }, label = { Text("Floor Number") }, modifier = Modifier.fillMaxWidth(), colors = getOutlinedTextFieldColors())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = blockName, onValueChange = { blockName = it }, label = { Text("Block Name") }, modifier = Modifier.fillMaxWidth(), colors = getOutlinedTextFieldColors())
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {
                if (room != null) {
                    viewModel.updateRoom(room.id, roomNumber, capacity, floor, blockName)
                    navController.popBackStack()
                }
            }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ADE80))) {
                Text("Save Changes", fontSize = 18.sp)
            }
        }
    }
}
