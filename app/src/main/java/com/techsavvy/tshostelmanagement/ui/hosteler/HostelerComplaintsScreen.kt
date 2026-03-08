package com.techsavvy.tshostelmanagement.ui.hosteler

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Delete // Added for delete icon
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
import com.techsavvy.tshostelmanagement.data.models.Complaint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostelerComplaintsScreen(
    navController: NavController,
    viewModel: ComplaintViewModel = hiltViewModel()
) {
    val complaints by viewModel.complaints.collectAsState()

    // State to handle deletion confirmation
    var complaintToDelete by remember { mutableStateOf<Complaint?>(null) }

    Scaffold(
        containerColor = Color(0xFF010413),
        topBar = {
            TopAppBar(
                title = { Text("My Complaints", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screens.Hosteler.RaiseComplaint.route) },
                containerColor = Color(0xFFF87171)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Raise Complaint", tint = Color.Black)
            }
        }
    ) { paddingValues ->

        // Deletion Confirmation Dialog
        if (complaintToDelete != null) {
            AlertDialog(
                onDismissRequest = { complaintToDelete = null },
                containerColor = Color(0xFF0F172A),
                title = { Text("Delete Complaint", color = Color.White) },
                text = { Text("Are you sure you want to remove this complaint? It will be soft-deleted (hidden from view, data preserved).", color = Color.Gray) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteComplaint(complaintToDelete!!.id)
                            complaintToDelete = null
                        }
                    ) {
                        Text("Delete", color = Color(0xFFF87171))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { complaintToDelete = null }) {
                        Text("Cancel", color = Color.White)
                    }
                }
            )
        }

        if (complaints.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Not any Complaints yet", color = Color.Gray, fontSize = 18.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp)) {
                items(complaints) { complaint ->
                    ComplaintItem(
                        complaint = complaint,
                        onDeleteClick = { complaintToDelete = complaint },
                        onCardClick = {
                            navController.navigate(Screens.Hosteler.ComplaintDetail.createRoute(complaint.id))
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun ComplaintItem(complaint: Complaint, onDeleteClick: () -> Unit, onCardClick: () -> Unit = {}) {
    Card(
        onClick = onCardClick,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = complaint.subject, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                // Row to hold status and delete icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = complaint.status,
                        color = if (complaint.status == "Resolved") Color(0xFF4ADE80) else Color(0xFFFACC15),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFF87171).copy(alpha = 0.7f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = complaint.message, color = Color.LightGray, fontSize = 14.sp)
        }
    }
}