package com.techsavvy.tshostelmanagement.ui.staff.complaints

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
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
        val context = LocalContext.current
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

                // ── Attachments ───────────────────────────────────────────
                if (complaint.mediaUrls.isNotEmpty()) {
                    Spacer(Modifier.height(24.dp))
                    Text("Attachments", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(12.dp))

                    var fullscreenUrl by remember { mutableStateOf<String?>(null) }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                    ) {
                        LazyRow(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(complaint.mediaUrls) { url ->
                                val isVideo = url.endsWith(".mp4", true) ||
                                    url.endsWith(".mov", true) ||
                                    url.endsWith(".avi", true) ||
                                    url.endsWith(".mkv", true) ||
                                    url.contains("/video")

                                if (isVideo) {
                                    Box(
                                        modifier = Modifier
                                            .size(90.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color.White.copy(alpha = 0.08f))
                                            .clickable {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                                                    setDataAndType(Uri.parse(url), "video/*")
                                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                }
                                                context.startActivity(intent)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Rounded.PlayCircle,
                                            contentDescription = "Play video",
                                            tint = Color(0xFF22D3EE),
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                } else {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = "Attachment",
                                        modifier = Modifier
                                            .size(90.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { fullscreenUrl = url },
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }

                    fullscreenUrl?.let { url ->
                        Dialog(onDismissRequest = { fullscreenUrl = null }) {
                            AsyncImage(
                                model = url,
                                contentDescription = "Full image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { fullscreenUrl = null },
                                contentScale = ContentScale.FillWidth
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}