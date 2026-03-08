package com.techsavvy.tshostelmanagement.ui.staff.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.techsavvy.tshostelmanagement.data.models.StaffTask
import com.techsavvy.tshostelmanagement.navigation.Screens
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: StaffHomeViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        containerColor = Color(0xFF010413),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Staff Dashboard", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Manage your assigned tasks", color = Color.Gray, fontSize = 14.sp)
                }

                Row {
                    IconButton(
                        onClick = { navController.navigate(Screens.Staff.Settings.route) },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(1.dp, Brush.linearGradient(listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)), CircleShape)
                    ) {
                        Icon(Icons.Rounded.Settings, contentDescription = "Settings", tint = Color.White.copy(alpha = 0.8f))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { navController.navigate(Screens.Staff.Profile.route) },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(1.dp, Brush.linearGradient(listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)), CircleShape)
                    ) {
                        Icon(Icons.Rounded.AccountCircle, contentDescription = "Profile", tint = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Main complaints button
            Button(
                onClick = { navController.navigate(Screens.Staff.Complaints.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, Brush.linearGradient(listOf(Color(0xFF22D3EE).copy(alpha = 0.5f), Color.Transparent)), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Assignment,
                        contentDescription = null,
                        tint = Color(0xFF22D3EE)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("See Complaints", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "My Tasks",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF22D3EE))
                }
            } else if (tasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tasks assigned yet", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding =paddingValues,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(tasks) { task ->
                        StaffTaskCard(
                            task = task,
                            onStatusChange = { newStatus -> viewModel.updateTaskStatus(task.id, newStatus) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StaffTaskCard(task: StaffTask, onStatusChange: (String) -> Unit) {
    val statusColor = when (task.status) {
        "Pending" -> Color(0xFFFACC15) // Yellow
        "In Progress" -> Color(0xFF6366F1) // Indigo
        "Completed" -> Color(0xFF4ADE80) // Green
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(statusColor.copy(alpha = 0.1f), Color.White.copy(alpha = 0.05f))
                    )
                )
                .border(
                    1.dp,
                    Brush.linearGradient(listOf(statusColor.copy(alpha = 0.4f), Color.Transparent)),
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.taskTitle,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    // Interactive Status Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(statusColor.copy(alpha = 0.2f))
                            .clickable {
                                // Cycle status
                                val nextStatus = when (task.status) {
                                    "Pending" -> "In Progress"
                                    "In Progress" -> "Completed"
                                    else -> "Pending"
                                }
                                onStatusChange(nextStatus)
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = task.status,
                            color = statusColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = task.description,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                val dateStr = SimpleDateFormat("dd MMM, yyyy • hh:mm a", Locale.getDefault()).format(Date(task.assignedAt))
                Text(
                    text = "Assigned: $dateStr",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}