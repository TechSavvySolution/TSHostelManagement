package com.techsavvy.tshostelmanagement.ui.admin.hostelers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.techsavvy.tshostelmanagement.data.models.User
import com.techsavvy.tshostelmanagement.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostellersScreen(
    navController: NavController,
    viewModel: HostellersViewModel = hiltViewModel()
) {
    var showMenu by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    var userToDelete by remember { mutableStateOf<User?>(null) }

    val allHostellers by viewModel.allHostellers.collectAsState()
    val assignedHostellers by viewModel.assignedHostellers.collectAsState(initial = emptyList())

    val currentList = if (selectedTab == 0) allHostellers else assignedHostellers
    val filteredUsers = currentList.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true)
    }

    // Soft-Delete Confirmation Dialog
    if (userToDelete != null) {
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            containerColor = Color(0xFF1E293B),
            title = { Text("Remove Hosteler?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "\"${userToDelete?.name}\" will be soft-deleted and hidden from all views. The record is preserved in Firestore.",
                    color = Color.Gray
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    userToDelete?.let { viewModel.softDeleteHosteler(it.uid) }
                    userToDelete = null
                }) { Text("Remove", color = Color(0xFFF87171), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) { Text("Cancel", color = Color.White) }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF010413),
        topBar = {
            TopAppBar(
                title = { Text("Hostellers", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                FloatingActionButton(
                    onClick = { showMenu = !showMenu },
                    containerColor = Color(0xFF4ADE80)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add User or Assign Hosteller", tint = Color.Black)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(Color(0xFF0F172A))
                ) {
                    DropdownMenuItem(
                        text = { Text("Add User", color = Color.White) },
                        onClick = { showMenu = false; navController.navigate("Screens.Admin.AddUser.route") }
                    )
                    DropdownMenuItem(
                        text = { Text("Assign Hosteller", color = Color.White) },
                        onClick = { showMenu = false; navController.navigate("Screens.Admin.AssignHosteller.route") }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search hosteller") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color(0xFF4ADE80),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == 0) Color(0xFF4ADE80) else Color(0xFF1E293B)
                    )
                ) {
                    Text("All Users", color = if (selectedTab == 0) Color.Black else Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == 1) Color(0xFF4ADE80) else Color(0xFF1E293B)
                    )
                ) {
                    Text("Assigned", color = if (selectedTab == 1) Color.Black else Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredUsers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    val emptyMessage = when {
                        searchQuery.isNotEmpty() -> "No results found"
                        selectedTab == 1 -> "No assigned hostellers yet"
                        else -> "No hostellers found"
                    }
                    Text(text = emptyMessage, color = Color.White)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredUsers) { user ->
                        UserListItem(
                            user = user,
                            onEditClick = { navController.navigate(Screens.Admin.EditUser.createRoute(user.uid)) },
                            onDeleteClick = { userToDelete = user }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserListItem(user: User, onEditClick: () -> Unit = {}, onDeleteClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF4ADE80)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (user.name.isNotEmpty()) user.name.first().toString().uppercase() else "?",
                    color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold
                )
            }

            Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                Text(text = user.name, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = user.email, color = Color.Gray, fontSize = 13.sp)
            }

            // EDIT button
            IconButton(onClick = onEditClick) {
                Icon(androidx.compose.material.icons.Icons.Default.Edit, "Edit Hosteler", tint = Color.LightGray)
            }
            // SOFT DELETE button
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Rounded.Delete, "Remove Hosteler", tint = Color(0xFFF87171))
            }
        }
    }
}

