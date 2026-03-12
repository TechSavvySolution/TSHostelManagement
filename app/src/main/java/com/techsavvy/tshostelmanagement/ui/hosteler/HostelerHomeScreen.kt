package com.techsavvy.tshostelmanagement.ui.hosteler.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.techsavvy.tshostelmanagement.navigation.Screens
import com.techsavvy.tshostelmanagement.ui.hosteler.fees.HostelerFeesViewModel
import com.techsavvy.tshostelmanagement.ui.hosteler.profile.AccentRed
import com.techsavvy.tshostelmanagement.ui.hosteler.profile.AccentRedDim

@Composable
fun HostelerHomeScreen(
    navController: NavController,
    viewModel: HostelerViewModel = hiltViewModel(),
    feesViewModel: HostelerFeesViewModel = hiltViewModel(),
    complaintViewModel: com.techsavvy.tshostelmanagement.ui.hosteler.ComplaintViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()
    val messMenu by viewModel.messMenu.collectAsState()
    val feeRecords by feesViewModel.feeRecords.collectAsState()
    val feesStatus = feesViewModel.feesStatusText
    val roomInfo by viewModel.roomInfo.collectAsState()
    val complaints by complaintViewModel.complaints.collectAsState()
    val latestComplaint = complaints.firstOrNull()
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color(0xFF010413),
        topBar = {
            HostelerTopBar(
                userName = user?.name ?: "Hosteler",
                onProfileClick = {
                    navController.navigate(Screens.Hosteler.Profile.route)
                },
                onSettingsClick = {
                    navController.navigate(Screens.Hosteler.Settings.route)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader(title = "Overview")

            Spacer(modifier = Modifier.height(16.dp))

            // Room Info Card
            if (roomInfo != null) {
                val (roomName, floorName, blockName) = roomInfo!!
                RoomInfoCard(roomName = roomName, floorName = floorName, blockName = blockName)
                Spacer(modifier = Modifier.height(16.dp))

                RoommatesButton(
                    onClick = { navController.navigate(Screens.Hosteler.Roommates.route) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoCard(
                    title = "Today",
                    value = viewModel.currentDate,
                    icon = Icons.Rounded.CalendarToday,
                    color = Color(0xFF22D3EE),
                    modifier = Modifier.weight(1f)
                )
                InfoCard(
                    title = "Fees Status",
                    value = feesStatus,
                    icon = Icons.Rounded.AttachMoney,
                    color = if (feesStatus == "Paid") Color(0xFF4ADE80) else Color(0xFFF87171),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { navController.navigate(Screens.Hosteler.Fees.route) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            MessMenuCard(menu = messMenu)

            Spacer(modifier = Modifier.height(24.dp))

            // UPDATED: Now navigates to the Hosteler Announcements route
            AnnouncementButton(
                onClick = { navController.navigate(Screens.Hosteler.Announcements.route) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(title = "Complaints")

            Spacer(modifier = Modifier.height(16.dp))

            ComplaintPreviewCard(
                latestComplaint = latestComplaint,
                onActionClick = {
                    navController.navigate(Screens.Hosteler.Complaints.route)
                }
            )

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun HostelerTopBar(
    userName: String,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column {
            Text(text = "Welcome back,", color = Color.Gray, fontSize = 14.sp)
            Text(text = userName, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .border(
                    1.dp,
                    Brush.linearGradient(listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)),
                    RoundedCornerShape(50.dp)
                )
                .padding(horizontal = 8.dp)
        ) {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Rounded.Tune,
                    contentDescription = "Settings",
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = "Profile",
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun RoomInfoCard(roomName: String, floorName: String, blockName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF8B5CF6).copy(alpha = 0.2f), Color(0xFF6366F1).copy(alpha = 0.2f))
                    )
                )
                .border(
                    1.dp,
                    Brush.linearGradient(listOf(Color(0xFF8B5CF6).copy(alpha = 0.5f), Color.Transparent)),
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF8B5CF6).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.MeetingRoom,
                        contentDescription = "Room Info",
                        tint = Color(0xFF8B5CF6)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "My Assignment",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$roomName • $floorName • $blockName",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun InfoCard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(16.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = title, color = Color.Gray, fontSize = 12.sp)
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun MessMenuCard(menu: com.techsavvy.tshostelmanagement.data.models.MessMenu) {
    // Get today's day name to show today's meals first
    val today = java.text.SimpleDateFormat("EEEE", java.util.Locale.getDefault()).format(java.util.Date())
    val todayMenu = when (today) {
        "Monday" -> menu.monday
        "Tuesday" -> menu.tuesday
        "Wednesday" -> menu.wednesday
        "Thursday" -> menu.thursday
        "Friday" -> menu.friday
        "Saturday" -> menu.saturday
        "Sunday" -> menu.sunday
        else -> menu.monday
    }
    val mealsList = listOf(
        "🌅 Breakfast" to todayMenu.breakfast,
        "☀️ Lunch" to todayMenu.lunch,
        "🌙 Dinner" to todayMenu.dinner
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(
                colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
            ))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.RestaurantMenu, contentDescription = null, tint = Color(0xFFFACC15))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Mess Menu", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Today — $today", color = Color.Gray, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        mealsList.forEachIndexed { index, (label, value) ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = label,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                    modifier = Modifier.width(100.dp),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (value.isBlank()) "—" else value,
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )
            }
            if (index < mealsList.size - 1) {
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            }
        }
    }
}

@Composable
fun ComplaintPreviewCard(
    latestComplaint: com.techsavvy.tshostelmanagement.data.models.Complaint?,
    onActionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(20.dp)
            .clickable(
                onClick = onActionClick
            )
        ,
    ) {
        if (latestComplaint != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val icon = when (latestComplaint.status.lowercase()) {
                    "resolved" -> Icons.Rounded.CheckCircle
                    "in progress", "processing", "pending" -> Icons.Rounded.Pending
                    else -> Icons.Rounded.Info
                }
                val iconTint = when (latestComplaint.status.lowercase()) {
                    "resolved" -> Color(0xFF4ADE80)
                    "in progress", "processing", "pending" -> Color(0xFFFACC15)
                    else -> Color(0xFF22D3EE)
                }

                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        latestComplaint.subject,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        "Status: ${latestComplaint.status}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("No active complaints", color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold)
                    Text("Everything looks good!", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onActionClick,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF87171))
        ) {
            Icon(Icons.Rounded.Add, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Raise New Complaint", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AnnouncementButton(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth().height(80.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(
                    colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                ))
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Announcements", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Check latest updates", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun RoommatesButton(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(80.dp),
        colors = CardDefaults.cardColors(containerColor = AccentRedDim),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(listOf(AccentRed.copy(alpha = 0.5f), Color.Transparent))
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AccentRed.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Group, null, tint = AccentRed)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("My Roommates", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("See who is sharing with you", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }
            }
            Icon(
                Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = AccentRed,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}