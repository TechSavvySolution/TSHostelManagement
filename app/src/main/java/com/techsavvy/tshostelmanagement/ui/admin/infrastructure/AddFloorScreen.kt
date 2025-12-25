package com.techsavvy.tshostelmanagement.ui.admin.infrastructure

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun AddFloorScreen(
    navController: NavController,
    viewModel: InfrastructureViewModel = hiltViewModel()
) {
    var floorNumber by remember { mutableStateOf("") }
    var blockName by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF010413)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Add New Floor",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = floorNumber,
                onValueChange = { floorNumber = it },
                label = { Text("Floor Number (e.g., 1)") },
                modifier = Modifier.fillMaxWidth(),
                colors = getOutlinedTextFieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = blockName,
                onValueChange = { blockName = it },
                label = { Text("Block Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = getOutlinedTextFieldColors()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.addFloor(floorNumber, blockName)
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ADE80))
            ) {
                Text(text = "Save Floor", fontSize = 18.sp)
            }
        }
    }
}
