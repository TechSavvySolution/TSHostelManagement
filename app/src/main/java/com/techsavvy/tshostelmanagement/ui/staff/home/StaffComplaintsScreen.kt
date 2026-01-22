package com.techsavvy.tshostelmanagement.ui.staff.complaints

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
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
import com.techsavvy.tshostelmanagement.data.models.Complaint
import com.techsavvy.tshostelmanagement.navigation.Screens
import com.techsavvy.tshostelmanagement.ui.admin.complaints.StatusChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffComplaintsScreen(
    navController: NavController,
    viewModel: StaffComplaintViewModel = hiltViewModel()
) {
    val complaints by viewModel.complaints.collectAsState()

    Scaffold(
        containerColor = Color(0xFF010413),
        topBar = {
            TopAppBar(
                title = { Text("My Assignments", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        if (complaints.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No complaints assigned to you", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(complaints) { complaint ->
                    StaffComplaintCard(
                        complaint = complaint,
                        onClick = {
                            navController.navigate(Screens.Staff.ComplaintDetails.createRoute(complaint.id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StaffComplaintCard(complaint: Complaint, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(complaint.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                StatusChip(status = complaint.status)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("From: ${complaint.userName}", color = Color(0xFF22D3EE), fontSize = 14.sp)
            Text("Location: Floor ${complaint.floor}, Room ${complaint.roomNo}", color = Color.Gray, fontSize = 12.sp)
        }
    }
}