package com.techsavvy.tshostelmanagement.ui.admin.fees

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeesScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Fees") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Fees Module")
        }
    }
}
