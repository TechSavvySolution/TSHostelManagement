package com.techsavvy.tshostelmanagement.ui.admin.complaints

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complaints") }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Complaints Module")
        }
    }
}
