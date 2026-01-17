package com.techsavvy.tshostelmanagement.ui.hosteler

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
        if (complaints.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Not any Complaints yet", color = Color.Gray, fontSize = 18.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(paddingValues).fillMaxSize().padding(16.dp)) {
                items(complaints) { complaint ->
                    ComplaintItem(complaint)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun ComplaintItem(complaint: Complaint) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = complaint.subject, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    text = complaint.status,
                    color = if (complaint.status == "Resolved") Color(0xFF4ADE80) else Color(0xFFFACC15),
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = complaint.message, color = Color.LightGray, fontSize = 14.sp)
        }
    }
}