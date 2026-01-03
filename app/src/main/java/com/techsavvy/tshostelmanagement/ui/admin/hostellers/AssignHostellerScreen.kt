package com.techsavvy.tshostelmanagement.ui.admin.hostellers

import androidx.compose.foundation.background
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.techsavvy.tshostelmanagement.data.models.Block
import com.techsavvy.tshostelmanagement.data.models.Floor
import com.techsavvy.tshostelmanagement.data.models.Room
import com.techsavvy.tshostelmanagement.data.models.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignHostellerScreen(
    navController: NavController,
    viewModel: AssignHostellerViewModel = hiltViewModel()
) {
    val users by viewModel.users.collectAsState()
    val blocks by viewModel.blocks.collectAsState()
    val floors by viewModel.floors.collectAsState()
    val rooms by viewModel.rooms.collectAsState()

    var selectedUser by remember { mutableStateOf<User?>(null) }
    var selectedBlock by remember { mutableStateOf<Block?>(null) }
    var selectedFloor by remember { mutableStateOf<Floor?>(null) }
    var selectedRoom by remember { mutableStateOf<Room?>(null) }
    var notes by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF010413),
        topBar = {
            TopAppBar(
                title = { Text("Assign Hosteller", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Assign a Hosteller to a Room",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))

            // User Dropdown
            Dropdown(label = "Select User", items = users, selectedItem = selectedUser, onItemSelected = { selectedUser = it }) { it.name }

            Spacer(modifier = Modifier.height(16.dp))

            // Block Dropdown
            Dropdown(label = "Select Block", items = blocks, selectedItem = selectedBlock, onItemSelected = {
                selectedBlock = it
                selectedFloor = null // Reset floor and room when block changes
                selectedRoom = null
                if (it != null) viewModel.fetchFloors(it.id)
            }) { it.name }

            Spacer(modifier = Modifier.height(16.dp))

            // Floor Dropdown
            Dropdown(label = "Select Floor", items = floors, selectedItem = selectedFloor, onItemSelected = {
                selectedFloor = it
                selectedRoom = null // Reset room when floor changes
                if (it != null) viewModel.fetchRooms(it.id)
            }, enabled = selectedBlock != null) { it.name }

            Spacer(modifier = Modifier.height(16.dp))

            // Room Dropdown
            Dropdown(label = "Select Room", items = rooms, selectedItem = selectedRoom, onItemSelected = { selectedRoom = it }, enabled = selectedFloor != null) { it.name }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color(0xFF4ADE80),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (selectedUser != null && selectedRoom != null) {
                        viewModel.assignHosteller(selectedUser!!.uid, selectedRoom!!.id, notes)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ADE80)),
                enabled = selectedUser != null && selectedRoom != null
            ) {
                Text(text = "Assign Hosteller", fontSize = 18.sp, color= Color.Black)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> Dropdown(
    label: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T?) -> Unit,
    enabled: Boolean = true,
    itemToString: (T) -> String
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { if(enabled) expanded = it }) {
        OutlinedTextField(
            readOnly = true,
            value = selectedItem?.let(itemToString) ?: "",
            onValueChange = {},
            label = { Text(label, color = Color.LightGray) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledTextColor = Color.Gray.copy(alpha = 0.7f),
                cursorColor = Color.White,
                focusedBorderColor = Color(0xFF4ADE80),
                unfocusedBorderColor = Color.Gray,
                disabledBorderColor = Color.DarkGray,
                focusedTrailingIconColor = Color.LightGray,
                unfocusedTrailingIconColor = Color.LightGray
            ),
            enabled = enabled
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF0F172A))
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(itemToString(item), color = Color.White) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}
