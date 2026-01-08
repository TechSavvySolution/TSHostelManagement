package com.techsavvy.tshostelmanagement.ui.admin.hostelers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.techsavvy.tshostelmanagement.data.models.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostellersScreen(
    navController: NavController,
    viewModel: HostellersViewModel = hiltViewModel()
) {
    var showMenu by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = All User, 1 = Assigned

    // Observe data from ViewModel
    val allHostellers by viewModel.allHostellers.collectAsState()
    val assignedHostellers by viewModel.assignedHostellers.collectAsState(initial = emptyList())

    // Decide which list to show based on the tab
    val currentList = if (selectedTab == 0) allHostellers else assignedHostellers

    // Apply search filter locally
    val filteredUsers = currentList.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF010413),
        topBar = {
            TopAppBar(
                title = { Text("Hostellers", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                FloatingActionButton(
                    onClick = { showMenu = !showMenu },
                    containerColor = Color(0xFF4ADE80)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add User or Assign Hosteller",
                        tint = Color.Black
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(Color(0xFF0F172A))
                ) {
                    DropdownMenuItem(
                        text = { Text("Add User", color = Color.White) },
                        onClick = {
                            showMenu = false
                            navController.navigate("Screens.Admin.AddUser.route")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Assign Hosteller", color = Color.White) },
                        onClick = {
                            showMenu = false
                            navController.navigate("Screens.Admin.AssignHosteller.route")
                        }
                    )
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            // 🔍 SEARCH BAR
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search hosteller") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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

            // 🔹 TABS: ALL USER | ASSIGNED
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == 0) Color(0xFF4ADE80) else Color(0xFF1E293B)
                    )
                ) {
                    Text("All User", color = if (selectedTab == 0) Color.Black else Color.White)
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

            // 🔹 LIST CONTENT
            if (filteredUsers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Dynamic Empty Message to help debug
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
                        UserListItem(user = user)
                    }
                }
            }
        }
    }
}

@Composable
fun UserListItem(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4ADE80)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (user.name.isNotEmpty()) user.name.first().toString().uppercase() else "?",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(text = user.name, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = user.email, color = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HostellersScreenPreview() {
    HostellersScreen(rememberNavController())
}