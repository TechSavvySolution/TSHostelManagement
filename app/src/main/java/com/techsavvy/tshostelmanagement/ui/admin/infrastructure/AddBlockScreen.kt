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
fun AddBlockScreen(
    navController: NavController, 
    viewModel: InfrastructureViewModel = hiltViewModel()
) {
    var blockName by remember { mutableStateOf("") }
    var blockAlias by remember { mutableStateOf("") }

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
                text = "Add New Block",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = blockName,
                onValueChange = { blockName = it },
                label = { Text("Block Name (e.g., Block A)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = blockAlias,
                onValueChange = { blockAlias = it },
                label = { Text("Block Alias (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.addBlock(blockName, blockAlias.ifBlank { null })
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ADE80))
            ) {
                Text(text = "Save Block", fontSize = 18.sp)
            }
        }
    }
}
//Define Goal -> Admin --> Infrastructure Module

//Detailed Structure explanation
//start with hostel management system in android jetpack compose with Firebase Firestore
//now we're working on Infrastructure Module in which below are major entities
//responsiblities.
//there will be Block(id,name,image,notes)
//now we've flours in flour Flour(id,name,image,block_id,notes)
//the block_id refers to blocks collection and Block
//now we've Room in rooms we've Room(id,name,capacity,image,flour_id,notes)
//now the relationship flaw is like Room->Flour->Block
//so now we're working on this kind of structure.
//so we can design on centralised viewModel -> InfrastrcutureViewModel
//and the major InfrastrcutreScreen
//then we've three different files for adding Block,Flour and Room
//then in main home screen we need one floting button which opens popup to navigate betwwen
//these utility screens
//in home we can show tab like UI in which three tabs Block,Flour,Rooms
//and one independent UI to see the actual hierarchy and one analytics component

//make attractive and fully responsive ui so admin don't have to worry about it's UX
//Jetpack Compose Kotlin and dagger hilt dependency Injection