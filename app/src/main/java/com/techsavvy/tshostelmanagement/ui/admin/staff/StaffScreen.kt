package com.techsavvy.tshostelmanagement.ui.admin.staff

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.techsavvy.tshostelmanagement.data.models.User
import com.techsavvy.tshostelmanagement.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffScreen(
    navController: NavController,
    viewModel: StaffViewModel = hiltViewModel()
) {
    var showMenu by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val staffList by viewModel.staffList.collectAsState()

    val filteredStaff = staffList.filter {
        it.name.contains(searchQuery, ignoreCase = true) || it.email.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF010413),
        topBar = {
            TopAppBar(
                title = { Text("Staff Management", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                FloatingActionButton(
                    onClick = { showMenu = !showMenu },
                    containerColor = Color(0xFFF87171)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Actions", tint = Color.Black)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(Color(0xFF0F172A))
                ) {
                    DropdownMenuItem(
                        text = { Text("Add Staff", color = Color.White) },
                        onClick = {
                            showMenu = false
                            // Navigating to Add Staff screen using centralized route
                            navController.navigate(Screens.Admin.AddStaff.route)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Assign Task", color = Color.White) },
                        onClick = {
                            showMenu = false
                            navController.navigate(Screens.Admin.AssignTask.route)
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search staff member...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFF87171),
                    unfocusedBorderColor = Color.Gray
                )
            )

            if (filteredStaff.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No staff members found", color = Color.White.copy(alpha = 0.6f))
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredStaff) { staff ->
                        StaffListItem(user = staff)
                    }
                }
            }
        }
    }
}

@Composable
fun StaffListItem(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B))), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFF87171)),
                contentAlignment = Alignment.Center
            ) {
                Text(user.name.take(1).uppercase(), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                Text(text = user.name, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = user.email, color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}