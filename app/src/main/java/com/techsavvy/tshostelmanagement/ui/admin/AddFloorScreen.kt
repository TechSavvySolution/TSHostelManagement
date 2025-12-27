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
fun AddFloorScreen(viewModel: HostelViewModel = hiltViewModel()) {
    val blocks by viewModel.blocks.collectAsState()
    var floorName by remember { mutableStateOf("") }
    var floorNumber by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedBlock by remember { mutableStateOf<com.techsavvy.tshostelmanagement.data.models.Block?>(null) }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selectedBlock?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Block") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                blocks.forEach {                  
                    DropdownMenuItem(text = { Text(it.name) }, onClick = {
                        selectedBlock = it
                        expanded = false
                    })
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = floorName,
            onValueChange = { floorName = it },
            label = { Text("Floor Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = floorNumber,
            onValueChange = { floorNumber = it },
            label = { Text("Floor Number") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                selectedBlock?.let {
                    viewModel.addFloor(floorName, floorNumber.toIntOrNull() ?: 0, description, it.id)
                    floorName = ""
                    floorNumber = ""
                    description = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedBlock != null
        ) {
            Text("Add Floor")
        }
    }
}
