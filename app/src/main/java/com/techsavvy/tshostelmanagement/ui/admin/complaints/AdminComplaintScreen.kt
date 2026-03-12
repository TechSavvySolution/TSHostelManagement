package com.techsavvy.tshostelmanagement.ui.admin.complaints

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.techsavvy.tshostelmanagement.data.models.Complaint
import android.content.Intent
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminComplaintScreen(
    navController: NavController,
    viewModel: AdminComplaintViewModel = hiltViewModel()
) {
    val complaints by viewModel.filteredComplaints.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var selectedComplaint by remember { mutableStateOf<Complaint?>(null) }
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFF010413),
        topBar = {
            AdminComplaintTopBar(
                searchQuery = searchQuery,
                onSearchChange = viewModel::onSearchQueryChange,
                onAnalyticsClick = { /* Navigate to Analytics Screen */ }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (complaints.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(complaints) { complaint ->
                        ComplaintAdminCard(
                            complaint = complaint,
                            onClick = {
                                selectedComplaint = complaint
                                showSheet = true
                            }
                        )
                    }
                }
            }
        }

        if (showSheet && selectedComplaint != null) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFF0F172A)
            ) {
                ComplaintManagementContent(
                    complaint = selectedComplaint!!,
                    viewModel = viewModel,
                    onUpdateStatus = { status ->
                        viewModel.updateStatus(selectedComplaint!!.id, status)
                        showSheet = false
                    },
                    onAssignStaff = { uid, name, phone ->
                        viewModel.assignStaff(selectedComplaint!!.id, uid, name, phone)
                        showSheet = false
                    },
                    onDelete = {
                        viewModel.deleteComplaint(selectedComplaint!!.id)
                        showSheet = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintManagementContent(
    complaint: Complaint,
    viewModel: AdminComplaintViewModel,
    onUpdateStatus: (String) -> Unit,
    onAssignStaff: (String, String, String) -> Unit,
    onDelete: () -> Unit
) {
    val staffList by viewModel.staffList.collectAsState()

    var staffUid by remember { mutableStateOf(complaint.assignedStaffUid ?: "") }
    var staffName by remember { mutableStateOf(complaint.assignedStaffName ?: "") }
    var staffPhone by remember { mutableStateOf(complaint.assignedStaffPhone ?: "") }

    Column(modifier = Modifier
        .padding(24.dp)
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
    ) {
        val context = LocalContext.current

        Text("User Details", color = Color(0xFF22D3EE), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        DetailRow("Name", complaint.userName)
        DetailRow("Email", complaint.userEmail)
        DetailRow("Phone", complaint.userPhone)
        DetailRow("Location", "Floor ${complaint.floor}, Room ${complaint.roomNo}")

        // ── Attachments ────────────────────────────────────────────────────
        if (complaint.mediaUrls.isNotEmpty()) {
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.White.copy(alpha = 0.1f))
            Text("Attachments", color = Color(0xFFF59E0B), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            var fullscreenUrl by remember { mutableStateOf<String?>(null) }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.White.copy(alpha = 0.1f))

        Text("Assign Staff", color = Color(0xFF22D3EE), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = viewModel.isStaffDropdownExpanded,
            onExpandedChange = { viewModel.isStaffDropdownExpanded = it }
        ) {
            OutlinedTextField(
                value = if (staffName.isEmpty()) "Select Staff Member" else staffName,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.isStaffDropdownExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Color(0xFF22D3EE)
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = viewModel.isStaffDropdownExpanded,
                onDismissRequest = { viewModel.isStaffDropdownExpanded = false },
                modifier = Modifier.background(Color(0xFF1E293B))
            ) {
                staffList.forEach { staff ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(staff.name, color = Color.White)
                                Text(staff.phone, color = Color.Gray, fontSize = 12.sp)
                            }
                        },
                        onClick = {
                            staffUid = staff.uid
                            staffName = staff.name
                            staffPhone = staff.phone
                            viewModel.isStaffDropdownExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        Button(
            onClick = { onAssignStaff(staffUid, staffName, staffPhone) },
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
            enabled = staffName.isNotEmpty()
        ) {
            Text("Assign & Mark In-Progress")
        }

        Button(
            onClick = { onUpdateStatus("Resolved") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ADE80))
        ) {
            Text("Mark as Resolved", color = Color.Black)
        }

        // Added Delete Functionality with a matching Red theme
        Button(
            onClick = onDelete,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF87171))
        ) {
            Text("Delete Complaint", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ComplaintAdminCard(complaint: Complaint, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(complaint.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Room: ${complaint.roomNo} | ${complaint.userName}", color = Color.Gray, fontSize = 12.sp)
            }
            StatusChip(status = complaint.status)
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text("$label: ", color = Color.Gray, modifier = Modifier.width(80.dp))
        Text(value, color = Color.White)
    }
}

@Composable
fun StatusChip(status: String) {
    val color = when(status) {
        "Pending" -> Color(0xFFF87171)
        "Resolved" -> Color(0xFF4ADE80)
        "In-Progress" -> Color(0xFF6366F1)
        else -> Color.Yellow
    }
    Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
        Text(status, color = color, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp)
    }
}

@Composable
fun AdminComplaintTopBar(searchQuery: String, onSearchChange: (String) -> Unit, onAnalyticsClick: () -> Unit) {
    Column(modifier = Modifier.background(Color(0xFF010413)).padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Admin Portal", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onAnalyticsClick) {
                Icon(Icons.Rounded.Analytics, "Analytics", tint = Color(0xFF22D3EE))
            }
        }
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            placeholder = { Text("Search by room, name or title...") },
            leadingIcon = { Icon(Icons.Rounded.Search, null) },
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun EmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No complaints found", color = Color.Gray)
    }
}