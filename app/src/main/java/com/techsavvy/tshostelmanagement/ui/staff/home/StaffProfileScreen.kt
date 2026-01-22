package com.techsavvy.tshostelmanagement.ui.staff.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffProfileScreen(
    navController: NavController,
    viewModel: StaffProfileViewModel = hiltViewModel()
) {
    val user = viewModel.userData

    Scaffold(
        containerColor = Color(0xFF010413),
        topBar = {
            TopAppBar(
                title = { Text("Staff Profile", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Person, null, tint = Color.White, modifier = Modifier.size(80.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (user != null) {
                if (viewModel.isEditMode) {
                    OutlinedTextField(
                        value = viewModel.editName,
                        onValueChange = { viewModel.editName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF22D3EE)
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = viewModel.editPhone,
                        onValueChange = { viewModel.editPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF22D3EE)
                        )
                    )
                } else {
                    Text(text = user.name, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(text = user.email, color = Color.Gray, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(32.dp))
                    ProfileInfoRow(label = "Designation", value = "Hostel Staff")
                    ProfileInfoRow(label = "Phone Number", value = user.phone)
                }

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = {
                        if (viewModel.isEditMode) viewModel.saveProfile()
                        else viewModel.toggleEditMode()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewModel.isEditMode) Color(0xFF4ADE80) else Color(0xFF22D3EE)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = if (viewModel.isEditMode) Icons.Rounded.Check else Icons.Rounded.Edit,
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (viewModel.isEditMode) "Save Changes" else "Edit Profile",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF22D3EE))
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp)
        Text(text = value, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}