package com.techsavvy.tshostelmanagement.ui.hosteler.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.techsavvy.tshostelmanagement.navigation.Screens

val AccentRed = Color(0xFFF87171)
val AccentRedDim = Color(0xFFF87171).copy(alpha = 0.15f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostelerProfileScreen(
    navController: NavController,
    viewModel: HostelerProfileViewModel = hiltViewModel()
) {
    val user = viewModel.userData
    val isUploading by viewModel.isUploading.collectAsState()
    val uploadError by viewModel.uploadError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uploadError) {
        if (!uploadError.isNullOrBlank()) {
            snackbarHostState.showSnackbar(uploadError!!)
            viewModel.clearUploadError()
        }
    }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? -> uri?.let { viewModel.pickAndUploadPhoto(it) } }

    Scaffold(
        containerColor = Color(0xFF010413),
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(snackbarData = data, containerColor = Color(0xFF7F1D1D), contentColor = Color.White)
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("My Profile", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                actions = {
                    TextButton(onClick = {
                        if (viewModel.isEditMode) viewModel.saveProfile()
                        else viewModel.toggleEditMode()
                    }) {
                        Text(
                            text = if (viewModel.isEditMode) "Save" else "Edit",
                            color = AccentRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Hero Header ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF3B0A0A), Color(0xFF010413))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(AccentRedDim)
                                .border(2.dp, AccentRed, CircleShape)
                                .clickable(enabled = viewModel.isEditMode) {
                                    photoPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            val photoUrl = user?.profilePhotoUrl
                            if (!photoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = photoUrl,
                                    contentDescription = "Profile photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    Icons.Rounded.Person, null,
                                    tint = AccentRed,
                                    modifier = Modifier.size(56.dp)
                                )
                            }
                        }
                        if (viewModel.isEditMode) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(AccentRed),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isUploading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        Icons.Rounded.AddAPhoto, null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = user?.name ?: "Loading...",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    // Role badge
                    Surface(
                        color = AccentRedDim,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Text(
                            "Hosteler",
                            color = AccentRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Edit Fields / Info Cards ─────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (viewModel.isEditMode) {
                    ProfileEditField(
                        value = viewModel.editName,
                        onValueChange = { viewModel.editName = it },
                        label = "Full Name",
                        accent = AccentRed
                    )
                    ProfileEditField(
                        value = viewModel.editPhone,
                        onValueChange = { viewModel.editPhone = it },
                        label = "Phone Number",
                        accent = AccentRed
                    )
                } else {
                    if (user != null) {
                        ProfileInfoCard(accent = AccentRed) {
                            ProfileInfoRow(Icons.Rounded.Person, "Full Name", user.name)
                            HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                            ProfileInfoRow(Icons.Rounded.Email, "Email", user.email)
                            HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                            ProfileInfoRow(Icons.Rounded.Phone, "Phone", user.phone)
                        }

                        // Room info card
                        if (viewModel.roomName.isNotBlank()) {
                            ProfileInfoCard(accent = AccentRed) {
                                ProfileInfoRow(Icons.Rounded.Hotel, "Room", viewModel.roomName)
                                HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                                ProfileInfoRow(Icons.Rounded.Layers, "Floor", viewModel.floorName)
                            }
                        }

                        // Roommates shortcut
                        Card(
                            onClick = { navController.navigate(Screens.Hosteler.Roommates.route) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = AccentRedDim),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Rounded.Group, null, tint = AccentRed, modifier = Modifier.size(22.dp))
                                Spacer(Modifier.width(12.dp))
                                Text("View My Roommates", color = AccentRed, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.weight(1f))
                                Icon(Icons.Rounded.ChevronRight, null, tint = AccentRed.copy(alpha = 0.6f))
                            }
                        }
                    } else {
                        Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = AccentRed)
                        }
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun ProfileInfoCard(accent: Color, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(1.dp, accent.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
        content = content
    )
}

@Composable
fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, color = Color.Gray, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ProfileEditField(value: String, onValueChange: (String) -> Unit, label: String, accent: Color) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = accent,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = accent,
            unfocusedLabelColor = Color.Gray
        )
    )
}