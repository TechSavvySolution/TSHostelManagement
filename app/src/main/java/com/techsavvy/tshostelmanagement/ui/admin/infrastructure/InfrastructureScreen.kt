package com.techsavvy.tshostelmanagement.ui.admin.infrastructure

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KingBed
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.techsavvy.tshostelmanagement.navigation.Screens

@Composable
fun InfrastructureScreen(
    navController: NavController,
    viewModel: InfrastructureViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isFabExpanded by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf<Pair<String, () -> Unit>?>(null) }
    var selectedTab by remember { mutableStateOf(0) }

    if (showDeleteConfirmation != null) {
        StyledConfirmationDialog(
            onConfirm = {
                showDeleteConfirmation?.second?.invoke()
                showDeleteConfirmation = null
            },
            onDismiss = { showDeleteConfirmation = null },
            title = "Confirm Deletion",
            text = "Are you sure you want to delete this ${showDeleteConfirmation?.first}? This action cannot be undone."
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF010413),
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AnimatedVisibility(visible = isFabExpanded) {
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        FabItem(icon = Icons.Default.Domain, label = "Add Block") { navController.navigate(Screens.Admin.AddBlock.route) }
                        FabItem(icon = Icons.Default.Apartment, label = "Add Floor") { navController.navigate(Screens.Admin.AddFloor.route) }
                        FabItem(icon = Icons.Default.KingBed, label = "Add Room") { navController.navigate(Screens.Admin.AddRoom.route) }
                    }
                }
                val rotation by animateFloatAsState(targetValue = if (isFabExpanded) 45f else 0f, label = "fab_rotation")
                FloatingActionButton(
                    onClick = { isFabExpanded = !isFabExpanded },
                    containerColor = Color(0xFF4ADE80),
                    shape = CircleShape
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add items",
                        tint = Color.Black,
                        modifier = Modifier.rotate(rotation)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize()
        ) {
            val filters = listOf("All", "Blocks", "Floors", "Rooms")
            LazyRow(
                modifier = Modifier.padding(vertical = 16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(filters) { index, title ->
                    val count = when (index) {
                        0 -> uiState.blocks.size + uiState.floors.size + uiState.rooms.size
                        1 -> uiState.blocks.size
                        2 -> uiState.floors.size
                        3 -> uiState.rooms.size
                        else -> 0
                    }
                    FilterChip(
                        title = title,
                        count = count,
                        isSelected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        if (uiState.blocks.isNotEmpty()) {
                            item(span = { GridItemSpan(2) }) {
                                SectionHeader(title = "Blocks")
                            }
                            items(uiState.blocks, key = { "block_${it.id}" }) { block ->
                                InfrastructureGridItem(
                                    item = InfrastructureItem.fromBlock(block, uiState),
                                    onEdit = { navController.navigate(Screens.Admin.EditBlock.createRoute(block.id)) },
                                    onDelete = { showDeleteConfirmation = Pair("block") { viewModel.deleteBlock(block.id) } }
                                )
                            }
                        }

                        if (uiState.floors.isNotEmpty()) {
                            item(span = { GridItemSpan(2) }) {
                                SectionHeader(title = "Floors")
                            }
                            items(uiState.floors, key = { "floor_${it.id}" }) { floor ->
                                InfrastructureGridItem(
                                    item = InfrastructureItem.fromFloor(floor),
                                    onEdit = { navController.navigate(Screens.Admin.EditFloor.createRoute(floor.id)) },
                                    onDelete = { showDeleteConfirmation = Pair("floor") { viewModel.deleteFloor(floor.id) } }
                                )
                            }
                        }

                        if (uiState.rooms.isNotEmpty()) {
                            item(span = { GridItemSpan(2) }) {
                                SectionHeader(title = "Rooms")
                            }
                            items(uiState.rooms, key = { "room_${it.id}" }) { room ->
                                InfrastructureGridItem(
                                    item = InfrastructureItem.fromRoom(room),
                                    onEdit = { navController.navigate(Screens.Admin.EditRoom.createRoute(room.id)) },
                                    onDelete = { showDeleteConfirmation = Pair("room") { viewModel.deleteRoom(room.id) } }
                                )
                            }
                        }
                    }
                    1 -> items(uiState.blocks, key = { "block_tab_${it.id}" }) { block ->
                        InfrastructureGridItem(
                            item = InfrastructureItem.fromBlock(block, uiState),
                            onEdit = { navController.navigate(Screens.Admin.EditBlock.createRoute(block.id)) },
                            onDelete = { showDeleteConfirmation = Pair("block") { viewModel.deleteBlock(block.id) } }
                        )
                    }
                    2 -> items(uiState.floors, key = { "floor_tab_${it.id}" }) { floor ->
                        InfrastructureGridItem(
                            item = InfrastructureItem.fromFloor(floor),
                            onEdit = { navController.navigate(Screens.Admin.EditFloor.createRoute(floor.id)) },
                            onDelete = { showDeleteConfirmation = Pair("floor") { viewModel.deleteFloor(floor.id) } }
                        )
                    }
                    3 -> items(uiState.rooms, key = { "room_tab_${it.id}" }) { room ->
                        InfrastructureGridItem(
                            item = InfrastructureItem.fromRoom(room),
                            onEdit = { navController.navigate(Screens.Admin.EditRoom.createRoute(room.id)) },
                            onDelete = { showDeleteConfirmation = Pair("room") { viewModel.deleteRoom(room.id) } }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChip(title: String, count: Int, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color(0xFF4ADE80) else Color.White.copy(alpha = 0.05f)
    val contentColor = if (isSelected) Color(0xFF010413) else Color.White.copy(alpha = 0.8f)

    Row(
        modifier = Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = title, color = contentColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color.Black.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color(0xFF4ADE80),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

data class InfrastructureItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color
) {
    companion object {
        fun fromBlock(block: Block, uiState: InfrastructureUiState) = InfrastructureItem(
            title = block.name,
            subtitle = "${uiState.getFloorCountForBlock(block.name)} Floors | ${uiState.getRoomCountForBlock(block.name)} Rooms",
            icon = Icons.Default.Domain,
            color = Color(0xFF22D3EE)
        )

        fun fromFloor(floor: Floor) = InfrastructureItem(
            title = "Floor ${floor.number}",
            subtitle = floor.blockName,
            icon = Icons.Default.Apartment,
            color = Color(0xFFA78BFA)
        )

        fun fromRoom(room: Room) = InfrastructureItem(
            title = "Room ${room.number}",
            subtitle = "Cap: ${room.capacity}",
            icon = Icons.Default.KingBed,
            color = Color(0xFFF87171)
        )
    }
}

@Composable
fun InfrastructureGridItem(
    item: InfrastructureItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .height(170.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .border(
                1.dp,
                Brush.linearGradient(listOf(item.color.copy(alpha = 0.5f), Color.Transparent)),
                RoundedCornerShape(24.dp)
            )
            .clickable { onEdit() }
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(item.color.copy(alpha = 0.1f))
                    .border(1.dp, item.color.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.color,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Box {
                IconButton(onClick = { isMenuExpanded = true }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White.copy(alpha = 0.7f))
                }
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false },
                    modifier = Modifier
                        .background(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    DropdownMenuItem(text = { Text("Edit", color = Color.White) }, onClick = { onEdit(); isMenuExpanded = false }, leadingIcon = { Icon(Icons.Default.Edit, "Edit", tint = Color.White.copy(alpha = 0.7f)) })
                    DropdownMenuItem(text = { Text("Delete", color = Color(0xFFE53935)) }, onClick = { onDelete(); isMenuExpanded = false }, leadingIcon = { Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFE53935)) })
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(text = item.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = item.subtitle, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
    }
}

@Composable
private fun FabItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .clickable(onClick = onClick)
            .background(Color.White.copy(alpha = 0.1f))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold)
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color(0xFF4ADE80)
        )
    }
}

@Composable
fun StyledConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title: String,
    text: String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E293B),
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color(0xFFFBBF24)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Text(
                text = text,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935).copy(alpha = 0.8f),
                    contentColor = Color.White
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color.White.copy(alpha = 0.5f))
                )
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}
