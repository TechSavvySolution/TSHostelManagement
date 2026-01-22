package com.techsavvy.tshostelmanagement.ui.staff.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Assignment
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Settings // Added for Settings/Logout access
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.techsavvy.tshostelmanagement.navigation.Screens

@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF010413)) // Deep dark navy
    ) {
        // Top Section with Settings and Profile Icons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Settings Button - Navigates to logout functionality
            IconButton(
                onClick = { navController.navigate(Screens.Staff.Settings.route) },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(
                        1.dp,
                        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = "Settings",
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Profile Button
            IconButton(
                onClick = { navController.navigate(Screens.Staff.Profile.route) },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(
                        1.dp,
                        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Rounded.AccountCircle,
                    contentDescription = "Profile",
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // Main Dashboard Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.Center)
        ) {
            Text(
                text = "Staff Dashboard",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Manage and resolve hostel tasks",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Main Action Button - Cyan Accent
            Button(
                onClick = { navController.navigate(Screens.Staff.Complaints.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        1.dp,
                        Brush.linearGradient(listOf(Color(0xFF22D3EE).copy(alpha = 0.5f), Color.Transparent)),
                        RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.05f)
                )
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
                    Text(
                        text = "See Complaints",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}