package com.techsavvy.tshostelmanagement.ui.admin.infrastructure

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KingBed
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
fun DetailsFloorScreen(
    navController: NavController,
    floorId: String?,
    viewModel: InfrastructureViewModel = hiltViewModel()
) {
    val floor by viewModel.selectedFloor.collectAsState()
    val rooms by viewModel.rooms.collectAsState()
    val studentsInFloor by viewModel.studentsInFloor.collectAsState()
    val showDeleteConfirmation = remember { mutableStateOf(false) }

    if (showDeleteConfirmation.value) {
        StyledConfirmationDialog(
            onConfirm = {
                if (floorId != null) {
                    viewModel.deleteItem("floor", floorId)
                    navController.popBackStack()
                }
                showDeleteConfirmation.value = false
            },
            onDismiss = { showDeleteConfirmation.value = false },
            title = "Confirm Deletion",
            text = "Are you sure you want to delete this floor? All rooms inside it will also be deleted. This action cannot be undone."
        )
    }

    LaunchedEffect(floorId) {
        if (floorId != null) {
            viewModel.getFloor(floorId)
            viewModel.getRoomsForFloor(floorId)
            viewModel.fetchStudentsForFloor(floorId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Floor Details") },
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
        if (floorId == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Floor not found.", color = Color.White)
            }
            return@Scaffold
        }

        val currentFloor = floor
        if (currentFloor != null) {
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
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
                                Text(currentFloor.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                StatusChip(status = Status.ACTIVE)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                StatCard(title = "Rooms", value = rooms.size.toString(), icon = Icons.Default.KingBed, modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Rooms section — upgraded styled cards
                    Text(
                        text = "Rooms in this Floor",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    if (rooms.isNotEmpty()) {
                        rooms.forEach { room ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { navController.navigate(Screens.Admin.DetailsRoom.createRoute(room.id)) },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.KingBed, contentDescription = "Room", tint = Color(0xFF4ADE80))
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(room.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                                        Text("Capacity: ${room.capacity}", color = Color.Gray, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    } else {
                        Text("No rooms found for this floor.", color = Color.White.copy(alpha = 0.7f))
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Students section
                    StudentsSectionHeader(count = studentsInFloor.size)
                    if (studentsInFloor.isEmpty()) {
                        StudentsEmptyState()
                    } else {
                        studentsInFloor.forEach { student ->
                            StudentMiniCard(student)
                            Spacer(Modifier.height(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Edit / Delete bottom bar
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.navigate(Screens.Admin.EditFloor.route + "/" + floorId) },
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
