package com.techsavvy.tshostelmanagement.ui.admin.hostellers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostellersScreen(navController: NavController) {
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Hostellers") })
        },
        floatingActionButton = {
            Box(
                modifier = Modifier.wrapContentSize(Alignment.TopEnd)
            ) {
                FloatingActionButton(onClick = { showMenu = !showMenu }) {
                    Icon(Icons.Default.Add, contentDescription = "Add User or Assign Hosteller")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Add User") },
                        onClick = {
                            showMenu = false
                            navController.navigate("add_user")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Assign Hosteller") },
                        onClick = {
                            showMenu = false
                            navController.navigate("assign_hosteller")
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No users have been added yet. Tap the '+' button to begin.")
        }
    }
}