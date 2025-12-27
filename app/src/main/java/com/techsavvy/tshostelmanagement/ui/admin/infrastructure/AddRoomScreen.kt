package com.techsavvy.tshostelmanagement.ui.admin.infrastructure

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.techsavvy.tshostelmanagement.data.models.Block
import com.techsavvy.tshostelmanagement.data.models.Floor
import com.techsavvy.tshostelmanagement.ui.hostel.HostelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoomScreen(
    navController: NavController,
    viewModel: HostelViewModel = hiltViewModel()
) {
    val blocks by viewModel.blocks.collectAsState()
    val floors by viewModel.floors.collectAsState()
    var roomName by remember { mutableStateOf("") }
    var roomNumber by remember { mutableStateOf("") }
    var roomCapacity by remember { mutableStateOf("") }
    var selectedBlock by remember { mutableStateOf<Block?>(null) }
    var selectedFloor by remember { mutableStateOf<Floor?>(null) }
    var blockExpanded by remember { mutableStateOf(false) }
    var floorExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF010413)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Add New Room",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))

            ExposedDropdownMenuBox(
                expanded = blockExpanded,
                onExpandedChange = { blockExpanded = !blockExpanded }
            ) {
                OutlinedTextField(
                    value = selectedBlock?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Block") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = blockExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = blockExpanded,
                    onDismissRequest = { blockExpanded = false }
                ) {
                    blocks.forEach { block ->
                        DropdownMenuItem(
                            text = { Text(block.name) },
                            onClick = {
                                selectedBlock = block
                                selectedFloor = null 
                                viewModel.getFloorsForBlock(block.id)
                                blockExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = floorExpanded,
                onExpandedChange = { floorExpanded = !floorExpanded }
            ) {
                OutlinedTextField(
                    value = selectedFloor?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Floor") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = floorExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    enabled = selectedBlock != null
                )
                ExposedDropdownMenu(
                    expanded = floorExpanded,
                    onDismissRequest = { floorExpanded = false }
                ) {
                    floors.forEach { floor ->
                        DropdownMenuItem(
                            text = { Text(floor.name) },
                            onClick = {
                                selectedFloor = floor
                                floorExpanded = false
                            }
                        )
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
                label = { Text("Room Number (e.g., 101)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = roomCapacity,
                onValueChange = { roomCapacity = it },
                label = { Text("Capacity") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    selectedBlock?.let { block ->
                        selectedFloor?.let { floor ->
                            viewModel.addRoom(
                                name = roomName,
                                roomNumber = roomNumber.toIntOrNull() ?: 0,
                                capacity = roomCapacity.toIntOrNull() ?: 0,
                                floorId = floor.id,
                                blockId = block.id
                            )
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = selectedFloor != null,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ADE80))
            ) {
                Text(text = "Save Room", fontSize = 18.sp)
            }
        }
    }
}
