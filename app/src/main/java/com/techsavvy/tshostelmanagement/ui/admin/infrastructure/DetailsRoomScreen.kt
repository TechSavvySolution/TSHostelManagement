package com.techsavvy.tshostelmanagement.ui.admin.infrastructure

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.techsavvy.tshostelmanagement.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsRoomScreen(
    navController: NavController,
    roomId: String?,
    viewModel: InfrastructureViewModel = hiltViewModel()
) {
    val room by viewModel.selectedRoom.collectAsState()
    val studentsInRoom by viewModel.studentsInRoom.collectAsState()
    val showDeleteConfirmation = remember { mutableStateOf(false) }

    if (showDeleteConfirmation.value) {
        StyledConfirmationDialog(
            onConfirm = {
                if (roomId != null) {
                    viewModel.deleteRoom(roomId)
                    navController.popBackStack()
                }
                showDeleteConfirmation.value = false
            },
            onDismiss = { showDeleteConfirmation.value = false },
            title = "Confirm Deletion",
            text = "Are you sure you want to delete this room? This action cannot be undone."
        )
    }

    LaunchedEffect(roomId) {
        if (roomId != null) {
            viewModel.getRoom(roomId)
            viewModel.fetchStudentsForRoom(roomId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Room Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF010413),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF010413)
    ) { paddingValues ->
        if (roomId == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Room not found.", color = Color.White)
            }
            return@Scaffold
        }

        val currentRoom = room
        if (currentRoom != null) {
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Room summary card
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(currentRoom.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                StatusChip(status = if (studentsInRoom.size >= currentRoom.capacity && currentRoom.capacity > 0) Status.FULL else Status.ACTIVE)
                            }
                            Spacer(Modifier.height(16.dp))
                            // Room detail rows
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                StatCard(
                                    title = "Capacity",
                                    value = currentRoom.capacity.toString(),
                                    icon = Icons.Default.Group,
                                    modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                    title = "Occupied",
                                    value = studentsInRoom.size.toString(),
                                    icon = Icons.Default.PersonPin,
                                    modifier = Modifier.weight(1f)
                                )
                                StatCard(
                                    title = "Available",
                                    value = maxOf(0, currentRoom.capacity - studentsInRoom.size).toString(),
                                    icon = Icons.Default.PersonAdd,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Room Number", color = Color.Gray, fontSize = 13.sp)
                                Text(currentRoom.roomNumber.toString(), color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Students staying here
                    StudentsSectionHeader(count = studentsInRoom.size)
                    if (studentsInRoom.isEmpty()) {
                        StudentsEmptyState()
                    } else {
                        studentsInRoom.forEach { student ->
                            StudentMiniCard(student)
                            Spacer(Modifier.height(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.navigate(Screens.Admin.EditRoom.route + "/" + roomId) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit")
                    }
                    Button(
                        onClick = { showDeleteConfirmation.value = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete")
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
