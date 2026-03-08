package com.techsavvy.tshostelmanagement.ui.staff.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.techsavvy.tshostelmanagement.navigation.Screens
import com.techsavvy.tshostelmanagement.ui.admin.settings.GlassmorphicCard
import com.techsavvy.tshostelmanagement.ui.admin.settings.GridBackground
import com.techsavvy.tshostelmanagement.ui.admin.settings.SettingItem
import com.techsavvy.tshostelmanagement.ui.admin.settings.SettingsCategory
import com.techsavvy.tshostelmanagement.ui.admin.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffSettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    var notificationsEnabled by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF010413))) {
        GridBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Settings", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Rounded.ArrowBack, "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    SettingsCategory(title = "Appearance")
                    GlassmorphicCard {
                        SettingItem(
                            icon = Icons.Rounded.WbSunny,
                            title = "Dark Mode",
                            subtitle = "Dark Glassmorphism is enforced",
                            trailingContent = {
                                Switch(
                                    checked = true,
                                    onCheckedChange = { },
                                    enabled = false,
                                    colors = SwitchDefaults.colors(
                                        disabledCheckedThumbColor = Color.LightGray,
                                        disabledCheckedTrackColor = Color(0xFF22D3EE).copy(alpha = 0.5f)
                                    )
                                )
                            }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }

                item {
                    SettingsCategory(title = "Account & Other")
                    GlassmorphicCard {
                        SettingItem(
                            icon = Icons.Rounded.Notifications,
                            title = "Notifications",
                            subtitle = "Manage notification preferences",
                            trailingContent = {
                                Switch(
                                    checked = notificationsEnabled,
                                    onCheckedChange = { notificationsEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF22D3EE)
                                    )
                                )
                            }
                        )
                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                        
                        SettingItem(
                            icon = Icons.Rounded.Info,
                            title = "About Application",
                            subtitle = "Developed by Tech Savvy Solution",
                            onClick = { navController.navigate(Screens.Staff.About.route) }
                        )

                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                        SettingItem(
                            icon = Icons.AutoMirrored.Rounded.Logout,
                            title = "Logout",
                            isDestructive = true,
                            onClick = {
                                viewModel.logout()
                                navController.navigate(Screens.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}