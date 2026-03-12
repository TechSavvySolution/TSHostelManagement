package com.techsavvy.tshostelmanagement.ui.hosteler

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.techsavvy.tshostelmanagement.data.models.Complaint
import com.techsavvy.tshostelmanagement.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostelerComplaintDetailScreen(
    navController: NavController,
    complaintId: String?,
    viewModel: ComplaintViewModel = hiltViewModel()
) {
    val complaints by viewModel.complaints.collectAsState()
    val complaint = remember(complaints, complaintId) {
        complaints.find { it.id == complaintId }
    }

    Scaffold(
        containerColor = Color(0xFF010413),
        topBar = {
            TopAppBar(
                title = { Text("Complaint Detail", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0F1E))
            )
        }
    ) { padding ->
        val context = LocalContext.current

        if (complaint == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6366F1))
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status Card
                StatusCard(complaint = complaint)

                // Complaint Details
                DetailCard(
                    title = "Your Complaint",
                    color = Color(0xFF6366F1)
                ) {
                    DetailRow(label = "Subject", value = complaint.subject)
                    DetailRow(label = "Category", value = complaint.title)
                    Spacer(Modifier.height(8.dp))
                    Text("Description", color = Color.Gray, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(complaint.message, color = Color.White, fontSize = 14.sp, lineHeight = 20.sp)
                }

                // Location Info
                DetailCard(title = "Location", color = Color(0xFF22D3EE)) {
                    DetailRow(label = "Floor", value = complaint.floor)
                    DetailRow(label = "Room No.", value = complaint.roomNo)
                }

                // Assigned Staff (if any)
                if (!complaint.assignedStaffName.isNullOrBlank()) {
                    DetailCard(title = "Assigned Staff", color = Color(0xFF4ADE80)) {
                        DetailRow(label = "Name", value = complaint.assignedStaffName)
                        DetailRow(label = "Phone", value = complaint.assignedStaffPhone ?: "-")
                    }
                }

                // Attachments section (only if complaint has media)
                if (complaint.mediaUrls.isNotEmpty()) {
                    DetailCard(
                        title = "Attachments",
                        color = Color(0xFFF59E0B)
                    ) {
                        var fullscreenUrl by remember { mutableStateOf<String?>(null) }

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(complaint.mediaUrls) { url ->
                                val isVideo = url.contains("/video") ||
                                    url.endsWith(".mp4", true) ||
                                    url.endsWith(".mov", true) ||
                                    url.endsWith(".avi", true) ||
                                    url.endsWith(".mkv", true)

                                if (isVideo) {
                                    // Video thumbnail card → tap to play
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
                                    // Image thumbnail → tap for fullscreen
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

                        // Fullscreen image dialog
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
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Chat Button (only shows if staff is assigned)
                if (!complaint.assignedStaffUid.isNullOrBlank()) {
                    Button(
                        onClick = {
                            navController.navigate(Screens.Hosteler.Chat.createRoute(complaint.id))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF6366F1), Color(0xFF22D3EE))
                                    ),
                                    RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Chat, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(10.dp))
                                Text("Chat with Staff", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                } else {
                    // Staff not yet assigned
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.04f))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Chat becomes available once staff is assigned",
                            color = Color.Gray,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun StatusCard(complaint: Complaint) {
    val statusColor = when (complaint.status) {
        "Resolved" -> Color(0xFF4ADE80)
        "In-Progress" -> Color(0xFF22D3EE)
        else -> Color(0xFFFACC15)
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(statusColor.copy(alpha = 0.08f))
            .border(1.dp, statusColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Status", color = Color.Gray, fontSize = 12.sp)
            Spacer(Modifier.height(4.dp))
            Text(complaint.status, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(statusColor, shape = RoundedCornerShape(50))
        )
    }
}

@Composable
fun DetailCard(title: String, color: Color, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(title, color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(2f))
    }
}
