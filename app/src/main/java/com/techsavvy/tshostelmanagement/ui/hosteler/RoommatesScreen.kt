package com.techsavvy.tshostelmanagement.ui.hosteler

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.techsavvy.tshostelmanagement.data.models.User

private val AccentRed = Color(0xFFF87171)
private val AccentRedDim = Color(0xFFF87171).copy(alpha = 0.12f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoommatesScreen(
    navController: NavController,
    viewModel: RoommatesViewModel = hiltViewModel()
) {
    val roommates by viewModel.roommates.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val roomName by viewModel.roomName.collectAsState()
    val floorName by viewModel.floorName.collectAsState()
    val blockName by viewModel.blockName.collectAsState()

    Scaffold(
        containerColor = Color(0xFF010413),
        topBar = {
            TopAppBar(
                title = { Text("My Roommates", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // ── Room Banner ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF3B0A0A), Color(0xFF1A0808)))
                    )
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(AccentRedDim)
                            .border(1.dp, AccentRed.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Hotel, null, tint = AccentRed, modifier = Modifier.size(28.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = if (roomName.isNotBlank()) "Room $roomName" else "My Room",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        val locationParts = buildList {
                            if (floorName.isNotBlank()) add("Floor $floorName")
                            if (blockName.isNotBlank()) add(blockName)
                        }
                        if (locationParts.isNotEmpty()) {
                            Text(
                                text = locationParts.joinToString(" · "),
                                color = Color.Gray,
                                fontSize = 13.sp
                            )
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    // Count badge
                    Surface(color = AccentRedDim, shape = CircleShape) {
                        Text(
                            text = "${roommates.size}",
                            color = AccentRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // ── Section Header ───────────────────────────────────────────────
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Group, null, tint = AccentRed, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Sharing with You",
                    color = AccentRed,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }

            // ── Content ──────────────────────────────────────────────────────
            when {
                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = AccentRed)
                    }
                }
                roommates.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Rounded.NightShelter, null,
                                tint = Color.Gray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text("You have the room to yourself!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("No other hostelers assigned to your room.", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(roommates) { user ->
                            RoommateCard(user)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoommateCard(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AccentRed.copy(alpha = 0.12f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.04f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(AccentRedDim)
                    .border(1.dp, AccentRed.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (user.profilePhotoUrl.isNotBlank()) {
                    AsyncImage(
                        model = user.profilePhotoUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = user.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        color = AccentRed,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.width(14.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Email, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(user.email, color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(Modifier.height(2.dp))
                if (user.phone.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Phone, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(user.phone, color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            // Roommate badge
            Surface(
                color = AccentRedDim,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Roommate",
                    color = AccentRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
