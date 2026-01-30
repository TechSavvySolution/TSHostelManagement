package com.techsavvy.tshostelmanagement.ui.hosteler.home

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

@Composable
fun HostelerHomeScreen(
    navController: NavController,
    viewModel: HostelerViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()
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
                    value = viewModel.feesStatus,
                    icon = Icons.Rounded.AttachMoney,
                    color = if (viewModel.feesStatus == "Paid") Color(0xFF4ADE80) else Color(0xFFF87171),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            MessMenuCard(menu = viewModel.messMenu)

            Spacer(modifier = Modifier.height(24.dp))

            // UPDATED: Now navigates to the Hosteler Announcements route
            AnnouncementButton(
                onClick = { navController.navigate(Screens.Hosteler.Announcements.route) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(title = "Complaints")

            Spacer(modifier = Modifier.height(16.dp))

            ComplaintPreviewCard(
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
fun MessMenuCard(menu: Map<String, String>) {
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
            Text("Mess Menu", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        menu.entries.forEachIndexed { index, entry ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = entry.key,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    modifier = Modifier.width(80.dp),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = entry.value,
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
            }
            if (index < menu.size - 1) {
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            }
        }
    }
}

@Composable
fun ComplaintPreviewCard(onActionClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("No active complaints", color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.SemiBold)
                Text("Everything looks good!", color = Color.Gray, fontSize = 12.sp)
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