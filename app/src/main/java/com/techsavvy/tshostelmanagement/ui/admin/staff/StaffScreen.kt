package com.techsavvy.tshostelmanagement.ui.admin.staff

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Staff") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Staff Module")
        }
    }
}
