package com.techsavvy.tshostelmanagement.ui.admin.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.techsavvy.tshostelmanagement.R



@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    // Launcher for selecting an image
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.onImageSelected(uri) }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF010413))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Profile",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Image Section with Change Functionality
            Box(contentAlignment = Alignment.BottomEnd) {
                if (viewModel.selectedImageUri != null) {
                    AsyncImage(
                        model = viewModel.selectedImageUri,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .clickable(enabled = viewModel.isEditMode) {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = "Default Profile Picture",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .clickable(enabled = viewModel.isEditMode) {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentScale = ContentScale.Crop
                    )
                }

                // Show a small camera icon overlay when in Edit Mode
                if (viewModel.isEditMode) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddAPhoto,
                            contentDescription = "Change Photo",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name Field
            if (viewModel.isEditMode) {
                OutlinedTextField(
                    value = viewModel.name,
                    onValueChange = { viewModel.onNameChange(it) },
                    label = { Text("Enter Name", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.Blue,
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            } else {
                Text(
                    text = viewModel.name,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = "admin@tshostel.com",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Edit/Save Button
            Button(
                onClick = {
                    if (viewModel.isEditMode) {
                        viewModel.saveProfile()
                    } else {
                        viewModel.toggleEditMode()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (viewModel.isEditMode) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (viewModel.isEditMode) Icons.Rounded.Check else Icons.Rounded.Edit,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = if (viewModel.isEditMode) "Save Changes" else "Edit Profile")
                }
            }
        }
    }
}