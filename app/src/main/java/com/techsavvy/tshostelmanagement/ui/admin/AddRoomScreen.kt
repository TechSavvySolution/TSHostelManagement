package com.techsavvy.tshostelmanagement.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techsavvy.tshostelmanagement.ui.hostel.HostelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoomScreen(viewModel: HostelViewModel = hiltViewModel()) {
    val blocks by viewModel.blocks.collectAsState()
    val floors by viewModel.floors.collectAsState()
    var roomName by remember { mutableStateOf("") }
    var roomNumber by remember { mutableStateOf("") }
    var roomCapacity by remember { mutableStateOf("") }
    var selectedBlock by remember { mutableStateOf<com.techsavvy.tshostelmanagement.data.models.Block?>(null) }
    var selectedFloor by remember { mutableStateOf<com.techsavvy.tshostelmanagement.data.models.Floor?>(null) }
    var blockExpanded by remember { mutableStateOf(false) }
    var floorExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ExposedDropdownMenuBox(expanded = blockExpanded, onExpandedChange = { blockExpanded = !blockExpanded }) {
            OutlinedTextField(
                value = selectedBlock?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Block") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = blockExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = blockExpanded, onDismissRequest = { blockExpanded = false }) {
                blocks.forEach {                  
                    DropdownMenuItem(text = { Text(it.name) }, onClick = {
                        selectedBlock = it
                        viewModel.getFloorsForBlock(it.id)
                        blockExpanded = false
                    })
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        ExposedDropdownMenuBox(expanded = floorExpanded, onExpandedChange = { floorExpanded = !floorExpanded }) {
            OutlinedTextField(
                value = selectedFloor?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Floor") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = floorExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                enabled = selectedBlock != null
            )
            ExposedDropdownMenu(expanded = floorExpanded, onDismissRequest = { floorExpanded = false }) {
                floors.forEach {                  
                    DropdownMenuItem(text = { Text(it.name) }, onClick = {
                        selectedFloor = it
                        floorExpanded = false
                    })
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = roomName,
            onValueChange = { roomName = it },
            label = { Text("Room Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = roomNumber,
            onValueChange = { roomNumber = it },
            label = { Text("Room Number") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = roomCapacity,
            onValueChange = { roomCapacity = it },
            label = { Text("Room Capacity") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                selectedFloor?.let {
                    viewModel.addRoom(
                        name = roomName,
                        roomNumber = roomNumber.toIntOrNull() ?: 0,
                        capacity = roomCapacity.toIntOrNull() ?: 0,
                        floorId = it.id,
                        blockId = it.blockId
                    )
                    roomName = ""
                    roomNumber = ""
                    roomCapacity = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedFloor != null
        ) {
            Text("Add Room")
        }
    }
}
