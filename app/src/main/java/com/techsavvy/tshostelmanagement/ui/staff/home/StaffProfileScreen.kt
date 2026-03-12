package com.techsavvy.tshostelmanagement.ui.staff.profile

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
import com.techsavvy.tshostelmanagement.ui.hosteler.profile.ProfileEditField
import com.techsavvy.tshostelmanagement.ui.hosteler.profile.ProfileInfoCard
import com.techsavvy.tshostelmanagement.ui.hosteler.profile.ProfileInfoRow

private val AccentCyan = Color(0xFF22D3EE)
private val AccentCyanDim = Color(0xFF22D3EE).copy(alpha = 0.15f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffProfileScreen(
    navController: NavController,
    viewModel: StaffProfileViewModel = hiltViewModel()
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
                Snackbar(snackbarData = data, containerColor = Color(0xFF083344), contentColor = Color.White)
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
                            color = AccentCyan,
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
                            listOf(Color(0xFF041F2C), Color(0xFF010413))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(AccentCyanDim)
                                .border(2.dp, AccentCyan, CircleShape)
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
                                    Icons.Rounded.Engineering, null,
                                    tint = AccentCyan,
                                    modifier = Modifier.size(56.dp)
                                )
                            }
                        }
                        if (viewModel.isEditMode) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(AccentCyan),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isUploading) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Rounded.AddAPhoto, null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(user?.name ?: "Loading...", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Surface(
                        color = AccentCyanDim,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Text(
                            "Hostel Staff",
                            color = AccentCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (viewModel.isEditMode) {
                    ProfileEditField(viewModel.editName, { viewModel.editName = it }, "Full Name", AccentCyan)
                    ProfileEditField(viewModel.editPhone, { viewModel.editPhone = it }, "Phone Number", AccentCyan)
                } else {
                    if (user != null) {
                        ProfileInfoCard(accent = AccentCyan) {
                            ProfileInfoRow(Icons.Rounded.Person, "Full Name", user.name)
                            HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                            ProfileInfoRow(Icons.Rounded.Email, "Email", user.email)
                            HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                            ProfileInfoRow(Icons.Rounded.Phone, "Phone", user.phone)
                        }
                        ProfileInfoCard(accent = AccentCyan) {
                            ProfileInfoRow(Icons.Rounded.Engineering, "Designation", "Hostel Staff")
                            HorizontalDivider(color = Color.White.copy(alpha = 0.06f))
                            ProfileInfoRow(Icons.Rounded.WorkHistory, "Access Level", "Staff Access")
                        }
                    } else {
                        Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = AccentCyan)
                        }
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}