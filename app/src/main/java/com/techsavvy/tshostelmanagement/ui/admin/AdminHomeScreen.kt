
package com.techsavvy.tshostelmanagement.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.techsavvy.tshostelmanagement.R
import com.techsavvy.tshostelmanagement.navigation.Screens

// Main Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Collect analytics data
    val totalBuildings by viewModel.totalBuildings.collectAsState()
    val totalFloors by viewModel.totalFloors.collectAsState()
    val totalRooms by viewModel.totalRooms.collectAsState()
    val totalHostellers by viewModel.totalHostellers.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.hsmlogo),
                            contentDescription = "Logo",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Admin Dashboard", fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                    IconButton(onClick = { navController.navigate(Screens.Admin.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A), // Card background from Login
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF020617) // Main background from Login
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { AnalyticsSection(totalBuildings, totalFloors, totalRooms, totalHostellers) }
            item { ManagementMenuSection(navController) }
            item { InfrastructureHeader(viewModel) }

            items(uiState.buildings, key = { it.id }) { building ->
                BuildingItem(building, uiState, viewModel)
            }
        }
    }

    // --- Dialogs ---
    if (uiState.showAddBuildingDialog) AddBuildingDialog(viewModel)
    if (uiState.showAddFloorDialog) AddFloorDialog(viewModel, uiState)
    if (uiState.showAddRoomDialog) AddRoomDialog(viewModel, uiState)
}

// --- Sections ---
@Composable
fun AnalyticsSection(totalBuildings: Int, totalFloors: Int, totalRooms: Int, totalHostellers: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Analytics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AnalyticsCard("Total Buildings", totalBuildings.toString(), Icons.Default.Apartment, Modifier.weight(1f))
            AnalyticsCard("Active Floors", totalFloors.toString(), Icons.Default.Layers, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AnalyticsCard("Total Rooms", totalRooms.toString(), Icons.Default.Bed, Modifier.weight(1f))
            AnalyticsCard("Hostellers", totalHostellers.toString(), Icons.Default.Person, Modifier.weight(1f))
        }
    }
}

@Composable
fun ManagementMenuSection(navController: NavController) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        MenuCard("Hostellers", Icons.Default.Person, Modifier.weight(1f)) { navController.navigate(Screens.Admin.Hostellers.route) }
        MenuCard("Staff", Icons.Default.Badge, Modifier.weight(1f)) { navController.navigate(Screens.Admin.Staff.route) }
    }
}

@Composable
fun InfrastructureHeader(viewModel: AdminViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Infrastructure", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
        Button(
            onClick = { viewModel.showDialog("building", true) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DD3FC)) // Accent color
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Building", tint = Color.Black)
            Spacer(Modifier.width(4.dp))
            Text("Add Block", color = Color.Black)
        }
    }
}

// --- Infrastructure Items ---
@Composable
fun BuildingItem(building: Building, uiState: AdminUiState, viewModel: AdminViewModel) {
    val isExpanded = uiState.expandedBuildingIds.contains(building.id)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            InfrastructureRow(
                text = building.name,
                icon = Icons.Default.Apartment,
                isExpanded = isExpanded,
                isSelected = uiState.selectedBuildingId == building.id,
                onClick = { viewModel.selectBuilding(building.id) },
                onExpandClick = { viewModel.toggleBuildingExpansion(building.id) }
            )

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)) {
                    building.description?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 12.dp)
                        )
                    }
                    building.floors.forEach { floor ->
                        FloorItem(floor, uiState, viewModel)
                    }
                    Button(
                        onClick = { viewModel.showDialog("floor", true) },
                        modifier = Modifier.padding(start = 24.dp, top = 8.dp),
                        enabled = uiState.selectedBuildingId == building.id,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DD3FC)) // Accent color
                    ) {
                        Text("Add Floor", color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun FloorItem(floor: Floor, uiState: AdminUiState, viewModel: AdminViewModel) {
    val isExpanded = uiState.expandedFloorIds.contains(floor.id)
    Column {
        InfrastructureRow(
            text = floor.number,
            icon = Icons.Default.Layers,
            isExpanded = isExpanded,
            isSelected = uiState.selectedFloorId == floor.id,
            onClick = { viewModel.selectFloor(floor.id) },
            onExpandClick = { viewModel.toggleFloorExpansion(floor.id) },
            modifier = Modifier.padding(start = 8.dp)
        )

        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(start = 16.dp)) {
                floor.rooms.forEach { room ->
                    RoomItem(room)
                }
                Button(
                    onClick = { viewModel.showDialog("room", true) },
                    modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 12.dp),
                    enabled = uiState.selectedFloorId == floor.id,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DD3FC)) // Accent color
                ) {
                    Text("Add Room", color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun RoomItem(room: Room) {
    InfrastructureRow(
        text = "Room ${room.number}",
        icon = Icons.Default.Bed,
        modifier = Modifier.padding(start = 16.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun dialogTextFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    cursorColor = Color(0xFF7DD3FC),
    focusedIndicatorColor = Color(0xFF7DD3FC),
    unfocusedIndicatorColor = Color.Gray,
    focusedLabelColor = Color(0xFF7DD3FC),
    unfocusedLabelColor = Color.Gray
)

// --- Dialogs ---
@Composable
fun AddBuildingDialog(viewModel: AdminViewModel) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    AlertDialog(
        containerColor = Color(0xFF0F172A),
        onDismissRequest = { viewModel.showDialog("building", false) },
        title = { Text("Add New Block", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Block Name (e.g., Block A)") },
                    colors = dialogTextFieldColors()
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    colors = dialogTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) viewModel.onAddBuilding(name, description.takeIf { it.isNotBlank() })
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DD3FC))
            ) {
                Text("Save", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.showDialog("building", false) }) {
                Text("Cancel", color = Color(0xFF7DD3FC))
            }
        }
    )
}

@Composable
fun AddFloorDialog(viewModel: AdminViewModel, uiState: AdminUiState) {
    var number by remember { mutableStateOf("") }
    val buildingName = uiState.buildings.find { it.id == uiState.selectedBuildingId }?.name ?: ""

    AlertDialog(
        containerColor = Color(0xFF0F172A),
        onDismissRequest = { viewModel.showDialog("floor", false) },
        title = { Text("Add Floor to $buildingName", color = Color.White) },
        text = {
            TextField(
                value = number,
                onValueChange = { number = it },
                label = { Text("Floor Number (e.g., 1st Floor)") },
                colors = dialogTextFieldColors()
            )
        },
        confirmButton = {
            Button(
                onClick = { if (number.isNotBlank()) viewModel.onAddFloor(number) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DD3FC))
            ) {
                Text("Save", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.showDialog("floor", false) }) {
                Text("Cancel", color = Color(0xFF7DD3FC))
            }
        }
    )
}

@Composable
fun AddRoomDialog(viewModel: AdminViewModel, uiState: AdminUiState) {
    var number by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    val floorName = uiState.buildings
        .firstOrNull { it.id == uiState.selectedBuildingId }?.floors
        ?.firstOrNull { it.id == uiState.selectedFloorId }?.number ?: ""

    AlertDialog(
        containerColor = Color(0xFF0F172A),
        onDismissRequest = { viewModel.showDialog("room", false) },
        title = { Text("Add Room to $floorName", color = Color.White) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = number,
                    onValueChange = { number = it },
                    label = { Text("Room Number") },
                    colors = dialogTextFieldColors()
                )
                TextField(
                    value = capacity,
                    onValueChange = { capacity = it },
                    label = { Text("Capacity (e.g., 4)") },
                    colors = dialogTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (number.isNotBlank() && capacity.isNotBlank()) {
                        viewModel.onAddRoom(number, capacity.toIntOrNull() ?: 0)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7DD3FC))
            ) {
                Text("Save", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.showDialog("room", false) }) {
                Text("Cancel", color = Color(0xFF7DD3FC))
            }
        }
    )
}

// --- Reusable Components ---
@Composable
fun AnalyticsCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = Color(0xFF7DD3FC), modifier = Modifier.size(32.dp)) // Accent color
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun MenuCard(title: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = Color(0xFF7DD3FC)) // Accent color
            Spacer(Modifier.width(8.dp))
            Text(title, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}

@Composable
fun InfrastructureRow(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isExpanded: Boolean? = null,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    onExpandClick: (() -> Unit)? = null,
) {
    val backgroundColor = if (isSelected) Color(0xFF7DD3FC).copy(alpha = 0.1f) else Color.Transparent
    val rotationAngle by animateFloatAsState(targetValue = if (isExpanded == true) 180f else 0f, label = "")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = text, tint = Color(0xFF7DD3FC), modifier = Modifier.size(20.dp)) // Accent color
        Spacer(Modifier.width(12.dp))
        Text(text, modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold, color = Color.White)
        isExpanded?.let {
            IconButton(onClick = { onExpandClick?.invoke() }) {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Expand",
                    modifier = Modifier.rotate(rotationAngle),
                    tint = Color.White
                )
            }
        }
    }
}
