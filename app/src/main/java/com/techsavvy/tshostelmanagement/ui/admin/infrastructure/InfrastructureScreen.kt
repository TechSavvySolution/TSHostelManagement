package com.techsavvy.tshostelmanagement.ui.admin.infrastructure

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.techsavvy.tshostelmanagement.data.models.Block
import com.techsavvy.tshostelmanagement.data.models.Floor
import com.techsavvy.tshostelmanagement.data.models.Room
import com.techsavvy.tshostelmanagement.navigation.Screens
import kotlinx.coroutines.flow.collectLatest

private data class FabMenuItemData(val icon: ImageVector, val label: String, val route: String)

@OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun InfrastructureScreen(
    navController: NavController,
    viewModel: InfrastructureViewModel = hiltViewModel()
) {
    val blocks by viewModel.blocks.collectAsState()
    val floors by viewModel.floors.collectAsState()
    val rooms by viewModel.rooms.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showMenu by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }


    LaunchedEffect(Unit) {
        viewModel.snackbarFlow.collectLatest {
            snackbarHostState.showSnackbar(it)
        }
    }

    var showDeleteConfirmation by remember { mutableStateOf<Pair<String, () -> Unit>?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) }

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

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color(0xFF010413),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showMenu = !showMenu },
                    containerColor = Color(0xFF4ADE80),
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, "Add items", tint = Color.Black)
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                val tabs = listOf(
                    "All" to Icons.Default.SelectAll,
                    "Blocks" to Icons.Default.Domain,
                    "Floors" to Icons.Default.Apartment,
                    "Rooms" to Icons.Default.KingBed
                )
                SearchBar(searchQuery = searchQuery, onQueryChange = { searchQuery = it }) { 
                    
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF4ADE80),
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            Box(
                                modifier = Modifier
                                    .tabIndicatorOffset(tabPositions[selectedTab])
                                    .height(3.dp)
                                    .background(color = Color(0xFF4ADE80), shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                            )
                        }
                    }
                ) {
                    tabs.forEachIndexed { index, (title, icon) ->
                        val count = when (index) {
                            0 -> blocks.size + floors.size + rooms.size
                            1 -> blocks.size
                            2 -> floors.size
                            3 -> rooms.size
                            else -> 0
                        }
                        val isSelected = selectedTab == index
                        Tab(
                            selected = isSelected,
                            onClick = { selectedTab = index },
                            modifier = Modifier.height(72.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                BadgedBox(badge = {
                                    if (count > 0) {
                                        Badge(
                                            containerColor = if (isSelected) Color(0xFF4ADE80) else Color.White.copy(alpha = 0.7f),
                                            contentColor = if (isSelected) Color(0xFF010413) else Color.Black
                                        ) {
                                            AnimatedContent(
                                                targetState = count,
                                                transitionSpec = {
                                                    (slideInVertically { height -> height } + fadeIn())
                                                        .togetherWith(slideOutVertically { height -> -height } + fadeOut())
                                                }, label = "count"
                                            ) { targetCount ->
                                                Text(
                                                    text = targetCount.toString(),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = title,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.animateContentSize()
                ) {
                    when (selectedTab) {
                        0 -> {
                            val allItems = (blocks.map { InfrastructureItem.fromBlock(it, floors, rooms) } +
                                    floors.map { InfrastructureItem.fromFloor(it) } +
                                    rooms.map { InfrastructureItem.fromRoom(it) })
                                .filter { it.title.contains(searchQuery, ignoreCase = true) }

                            if (allItems.isEmpty()) {
                                item(span = { GridItemSpan(2) }) {
                                    EmptyContent(
                                        title = "No items found",
                                        subtitle = "Tap the + button to add a new item.",
                                        icon = Icons.Default.MapsHomeWork
                                    )
                                }
                            } else {
                                items(allItems, key = { "${it.type}_${it.id}" }) { item ->
                                    InfrastructureGridItem(
                                        item = item,
                                        onEdit ={ when (item.type.lowercase()) {
                                            "block" -> {
                                                navController.navigate(
                                                    "${Screens.Admin.EditBlock.route}/${item.id}"
                                                )
                                            }

                                            "floor" -> {
                                                navController.navigate(
                                                    "${Screens.Admin.EditFloor.route}/${item.id}"
                                                )
                                            }

                                            "room" -> {
                                                navController.navigate(
                                                    "${Screens.Admin.EditRoom.route}/${item.id}"
                                                )
                                            }
                                        }
                                        },
                                        onDelete = { showDeleteConfirmation = Pair(item.type) { viewModel.deleteItem(item.type, item.id) } },
                                        onViewDetails = {
                                            when (item.type.lowercase()) {

                                                "block" -> {
                                                    navController.navigate(
                                                        "${Screens.Admin.DetailsBlock.route}/${item.id}"
                                                    )
                                                }

                                                "floor" -> {
                                                    navController.navigate(
                                                        "${Screens.Admin.DetailsFloor.route}/${item.id}"
                                                    )
                                                }

                                                "room" -> {
                                                    navController.navigate(
                                                        "${Screens.Admin.DetailsRoom.route}/${item.id}"
                                                    )
                                                }
                                            }
                                        }

                                    )
                                }
                            }
                        }
                        1 -> {
                            val filteredBlocks = blocks.filter { it.name.contains(searchQuery, ignoreCase = true) }
                            if (filteredBlocks.isEmpty()) {
                                item(span = { GridItemSpan(2) }) {
                                    EmptyContent(
                                        title = "No blocks found",
                                        subtitle = "Tap the + button to add a new block.",
                                        icon = Icons.Default.Domain
                                    )
                                }
                            } else {
                                items(filteredBlocks, key = { "block_tab_${it.id}" }) { block ->
                                    InfrastructureGridItem(
                                        item = InfrastructureItem.fromBlock(block, floors, rooms),
                                        onEdit = { navController.navigate(Screens.Admin.EditBlock.route + "/" + block.id)},
                                        onDelete = { showDeleteConfirmation = Pair("block") { viewModel.deleteBlock(block.id) } },
                                        onViewDetails = { navController.navigate(Screens.Admin.DetailsBlock.route + "/" + block.id) }
                                    )
                                }
                            }
                        }
                        2 -> {
                            if (floors.isEmpty()) {
                                item(span = { GridItemSpan(2) }) {
                                    EmptyContent(
                                        title = "No floors found",
                                        subtitle = "Tap + to create your first floor.",
                                        icon = Icons.Default.Apartment
                                    )
                                }
                            } else {
                                items(floors, key = { "floor_tab_${it.id}" }) { floor ->
                                    InfrastructureGridItem(
                                        item = InfrastructureItem.fromFloor(floor),
                                        onEdit = { navController.navigate(Screens.Admin.EditFloor.route + "/" + floor.id) },
                                        onDelete = { showDeleteConfirmation = Pair("floor") { viewModel.deleteFloor(floor.id) } },
                                        onViewDetails = { navController.navigate(Screens.Admin.DetailsFloor.route + "/" + floor.id) }
                                    )
                                }
                            }
                        }
                        3 -> {
                            val filteredRooms = rooms.filter { it.name.contains(searchQuery, ignoreCase = true) }
                            if (filteredRooms.isEmpty()) {
                                item(span = { GridItemSpan(2) }) {
                                    EmptyContent(
                                        title = "No rooms found",
                                        subtitle = "Tap the + button to add a new room.",
                                        icon = Icons.Default.KingBed
                                    )
                                }
                            } else {
                                items(filteredRooms, key = { "room_tab_${it.id}" }) { room ->
                                    InfrastructureGridItem(
                                        item = InfrastructureItem.fromRoom(room),
                                        onEdit = { navController.navigate(Screens.Admin.EditRoom.route + "/" + room.id) },
                                        onDelete = { showDeleteConfirmation = Pair("room") { viewModel.deleteRoom(room.id) } },
                                        onViewDetails = { navController.navigate(Screens.Admin.DetailsRoom.route + "/" + room.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showMenu = false }
            ) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 80.dp, end = 16.dp)
                        .width(IntrinsicSize.Max)
                        .background(Color(0xFF1E293B), RoundedCornerShape(16.dp))
                        .padding(vertical = 8.dp)
                ) {
                    val menuItems = listOf(
                        FabMenuItemData(Icons.Default.Domain, "Add Block", Screens.Admin.AddBlock.route),
                        FabMenuItemData(Icons.Default.Apartment, "Add Floor", Screens.Admin.AddFloor.route),
                        FabMenuItemData(Icons.Default.KingBed, "Add Room", Screens.Admin.AddRoom.route),
                    )

                    menuItems.forEach { item ->
                        FabMenuItem(
                            icon = item.icon,
                            text = item.label,
                            onClick = {
                                navController.navigate(item.route)
                                showMenu = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FabMenuItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = text, tint = Color(0xFF4ADE80))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, color = Color.White)
    }
}

@Composable
private fun EmptyContent(modifier: Modifier = Modifier, title: String, subtitle: String, icon: ImageVector) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.3f),
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = subtitle,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

data class InfrastructureItem(
    val id: String,
    val type: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val status: Status
) {
    companion object {
        fun fromBlock(block: Block, floors: List<Floor>, rooms: List<Room>) = InfrastructureItem(
            id = block.id,
            type = "block",
            title = block.name,
            subtitle = "${floors.count { it.blockId == block.id }} Floors | ${rooms.count { it.blockId == block.id }} Rooms",
            icon = Icons.Default.Domain,
            color = Color(0xFF3B82F6),
            status = Status.ACTIVE
        )

        fun fromFloor(floor: Floor) = InfrastructureItem(
            id = floor.id,
            type = "floor",
            title = "${floor.name}",
            subtitle = floor.blockId.substring(0,4),
            icon = Icons.Default.Apartment,
            color = Color(0xFF8B5CF6),
            status = Status.ACTIVE
        )

        fun fromRoom(room: Room) = InfrastructureItem(
            id = room.id,
            type = "room",
            title = "Room ${room.name}",
            subtitle = "Cap: ${room.capacity}",
            icon = Icons.Default.KingBed,
            color = Color(0xFFEF4444),
            status = if (room.capacity > 3) Status.FULL else Status.ACTIVE
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InfrastructureGridItem(
    item: InfrastructureItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewDetails: () -> Unit
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.98f else 1f, label = "scale")
    val shadowElevation by animateDpAsState(targetValue = if (isPressed) 16.dp else 8.dp, label = "shadow")

    Column(
        modifier = Modifier
            .scale(scale)
            .height(170.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.04f),
                        Color.White.copy(alpha = 0.02f)
                    )
                )
            )
            .border(
                1.dp,
                Brush.linearGradient(listOf(item.color.copy(alpha = 0.2f), Color.Transparent)),
                RoundedCornerShape(16.dp)
            )
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onEdit() },
                onLongClick = { isMenuExpanded = true }
            )
            .padding(20.dp)
            .shadow(
                elevation = 0.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = item.color.copy(alpha = 0.3f),
                spotColor = item.color.copy(alpha = 0.3f)
            )
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(item.color.copy(alpha = 0.1f))
                    .border(1.dp, item.color.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
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
                StatusChip(status = item.status)
                DropdownMenu(expanded = isMenuExpanded, onDismissRequest = { isMenuExpanded = false }) {
                    DropdownMenuItem(text = { Text("Edit") }, onClick = {
                        onEdit()
                        isMenuExpanded = false
                    })
                    DropdownMenuItem(text = { Text("Delete") }, onClick = {
                        onDelete()
                        isMenuExpanded = false
                    })
                    DropdownMenuItem(text = { Text("View Details") }, onClick = {
                        onViewDetails()
                        isMenuExpanded = false
                    })
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(item.color)
            )
            Text(text = item.title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = item.subtitle, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp, modifier = Modifier.padding(start = 16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(searchQuery: String, onQueryChange: (String) -> Unit, onSearch: () -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onQueryChange,
        placeholder = { Text("Search by name...", color = Color.White.copy(alpha = 0.5f)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = Color.White)
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            onSearch()
            keyboardController?.hide()
        }),
        textStyle = TextStyle(color = Color.White),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF4ADE80),
            unfocusedBorderColor = Color.Transparent,
            cursorColor = Color.White
        )
    )
}
