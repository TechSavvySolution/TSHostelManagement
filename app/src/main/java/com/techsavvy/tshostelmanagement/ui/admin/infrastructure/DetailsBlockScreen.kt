package com.techsavvy.tshostelmanagement.ui.admin.infrastructure

import androidx.compose.foundation.clickable
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.techsavvy.tshostelmanagement.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsBlockScreen(
    navController: NavController,
    blockId: String?,
    viewModel: InfrastructureViewModel = hiltViewModel()
) {
    val block by viewModel.selectedBlock.collectAsState()
    val floors by viewModel.floors.collectAsState()
    val rooms by viewModel.rooms.collectAsState()
    val showDeleteConfirmation = remember { mutableStateOf(false) }

    if (showDeleteConfirmation.value) {
        StyledConfirmationDialog(
            onConfirm = {
                if (blockId != null) {
                    // Before deleting the block, we must delete all floors and rooms within it
                    // to prevent orphaned data in your database.
                    floors.forEach { floor ->
                        val roomsOnThisFloor = rooms.filter { it.floorId == floor.id }
                        roomsOnThisFloor.forEach { room ->
                            viewModel.deleteItem("room", room.id)
                        }
                        viewModel.deleteItem("floor", floor.id)
                    }
                    viewModel.deleteItem("block", blockId)
                    navController.popBackStack()
                }
                showDeleteConfirmation.value = false
            },
            onDismiss = { showDeleteConfirmation.value = false },
            title = "Confirm Deletion",
            text = "Are you sure you want to delete this block? This action cannot be undone."
        )
    }

    LaunchedEffect(blockId) {
        if (blockId != null) {
            viewModel.getBlock(blockId)
            viewModel.getFloorsForBlock(blockId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Block Details") },
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
        if (blockId == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Block not found.", color = Color.White)
            }
            return@Scaffold
        }

        val currentBlock = block
        if (currentBlock != null) {
            val roomsInBlock = rooms.filter { room -> floors.any { floor -> floor.id == room.floorId } }
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = currentBlock.name,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                StatusChip(status = Status.ACTIVE)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                StatCard(title = "Floors", value = floors.size.toString(), icon = Icons.Default.Apartment, modifier = Modifier.weight(1f))
                                StatCard(title = "Rooms", value = roomsInBlock.size.toString(), icon = Icons.Default.KingBed, modifier = Modifier.weight(1f))
                                StatCard(title = "Capacity", value = roomsInBlock.sumOf { it.capacity }.toString(), icon = Icons.Default.Group, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // Block Information Section
                    Text(
                        text = "Block Information",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Block Name", color = Color.White.copy(alpha = 0.7f))
                        Text(currentBlock.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                    currentBlock.alias?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Alias", color = Color.White.copy(alpha = 0.7f))
                            Text(it, color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Floor Preview Section
                    Text(
                        text = "Floors in this Block",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    if (floors.isNotEmpty()) {
                        floors.forEach { floor ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { navController.navigate("details_floor/${floor.id}") },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Apartment, contentDescription = "Floor", tint = Color(0xFF4ADE80))
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(floor.name, color = Color.White, fontWeight = FontWeight.SemiBold)
                                        Text("${rooms.count { it.floorId == floor.id }} Rooms", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    } else {
                        Text("No floors found for this block.", color = Color.White.copy(alpha = 0.7f))
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Actions Section
                    Text(
                        text = "Quick Actions",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedButton(onClick = { navController.navigate("${Screens.Admin.AddFloor.route}")}, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Add, contentDescription = "Add Floor")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Floor")
                        }
                        OutlinedButton(onClick = { navController.navigate("${Screens.Admin.AddRoom.route}") }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Add, contentDescription = "Add Room")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Room")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { /* Handle View Complaints */ }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Report, contentDescription = "View Complaints")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Complaints")
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    OutlinedButton(
                        onClick = { navController.navigate(Screens.Admin.EditBlock.route + "/" + blockId) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit")
                    }
                    Button(
                        onClick = {
                            showDeleteConfirmation.value = true
                        },
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
