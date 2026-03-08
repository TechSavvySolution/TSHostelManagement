package com.techsavvy.tshostelmanagement.ui.staff.complaints

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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
import com.techsavvy.tshostelmanagement.ui.admin.complaints.DetailRow
import com.techsavvy.tshostelmanagement.ui.admin.complaints.StatusChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffComplaintDetailsScreen(
    navController: NavController,
    complaintId: String?,
    viewModel: StaffComplaintViewModel = hiltViewModel()
) {
    val complaints by viewModel.complaints.collectAsState()
    val complaint = remember(complaints, complaintId) {
        complaints.find { it.id == complaintId }
    }

    Scaffold(
        containerColor = Color(0xFF010413),
        topBar = {
            TopAppBar(
                title = { Text("Complaint Details", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            if (complaint != null && complaint.status != "Resolved") {
                Surface(
                    color = Color(0xFF010413),
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.updateStatus(complaint.id, "Resolved")
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ADE80)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Mark as Resolved", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    ) { padding ->
        if (complaint == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF4ADE80))
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with Status and Chat
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Current Status", color = Color.Gray, fontSize = 12.sp)
                            Spacer(Modifier.height(4.dp))
                            StatusChip(status = complaint.status)
                        }
                        Button(
                            onClick = {
                                navController.navigate(Screens.Staff.Chat.createRoute(complaint.id))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22D3EE)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Rounded.Chat, null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Chat", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Hosteler Info Section
                Text("Hosteler Information", color = Color(0xFF4ADE80), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        DetailRow("Name", complaint.userName)
                        DetailRow("Room", "${complaint.roomNo} (Floor ${complaint.floor})")
                        DetailRow("Phone", complaint.userPhone)
                        DetailRow("Email", complaint.userEmail)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Complaint Subject and Message
                Text("Issue Details", color = Color(0xFF4ADE80), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(complaint.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(complaint.message, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}