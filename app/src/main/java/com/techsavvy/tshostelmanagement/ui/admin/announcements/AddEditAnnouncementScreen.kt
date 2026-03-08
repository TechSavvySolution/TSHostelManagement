package com.techsavvy.tshostelmanagement.ui.admin.announcements

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import android.widget.Toast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAnnouncementScreen(
    navController: NavController,
    announcementId: String? = null,
    viewModel: AdminAnnouncementViewModel = hiltViewModel()
) {
    // If an ID is provided, load the existing announcement data from Firestore
    LaunchedEffect(announcementId) {
        announcementId?.let { viewModel.loadAnnouncement(it) }
    }
    
    val context = LocalContext.current

    Scaffold(
        containerColor = Color(0xFF010413),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (announcementId == null) "New Announcement" else "Edit Announcement",
                        color = Color.White
                    )
                },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title Input
            AnnouncementTextField(
                value = viewModel.title,
                onValueChange = { viewModel.title = it },
                label = "Title"
            )

            // Description Input (Multi-line)
            AnnouncementTextField(
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                label = "Description",
                singleLine = false,
                modifier = Modifier.height(150.dp)
            )

            // Image URL Input
            AnnouncementTextField(
                value = viewModel.imageUrl,
                onValueChange = { viewModel.imageUrl = it },
                label = "Image URL (Optional)"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sorting Order Input
                AnnouncementTextField(
                    value = viewModel.order,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) viewModel.order = it
                    },
                    label = "Sort Order",
                    modifier = Modifier.weight(1f)
                )

                // Active/Inactive Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Checkbox(
                        checked = viewModel.isActive,
                        onCheckedChange = { viewModel.isActive = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF4ADE80),
                            uncheckedColor = Color.Gray
                        )
                    )
                    Text("Is Active", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save Button
            Button(
                onClick = {
                    if (viewModel.title.isBlank() || viewModel.description.isBlank()) {
                        Toast.makeText(context, "Title and Description are required!", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.saveAnnouncement(announcementId) {
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ADE80))
            ) {
                Icon(Icons.Default.Check, null, tint = Color.Black)
                Spacer(Modifier.width(8.dp))
                Text("Save Announcement", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AnnouncementTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray) },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color(0xFF4ADE80),
            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
            cursorColor = Color(0xFF4ADE80)
        )
    )
}