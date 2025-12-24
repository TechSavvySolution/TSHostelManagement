package com.techsavvy.tshostelmanagement.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.techsavvy.tshostelmanagement.ui.admin.AdminHomeScreen
import com.techsavvy.tshostelmanagement.ui.admin.addBlock.AddBlockScreen
import com.techsavvy.tshostelmanagement.ui.admin.addFloor.AddFloorScreen
import com.techsavvy.tshostelmanagement.ui.admin.addRoom.AddRoomScreen
import com.techsavvy.tshostelmanagement.ui.admin.blocks.BlocksScreen
import com.techsavvy.tshostelmanagement.ui.admin.floors.FloorsScreen
import com.techsavvy.tshostelmanagement.ui.admin.hostellers.HostellersScreen
import com.techsavvy.tshostelmanagement.ui.admin.rooms.RoomsScreen
import com.techsavvy.tshostelmanagement.ui.admin.settings.SettingsScreen
import com.techsavvy.tshostelmanagement.ui.admin.staff.StaffScreen
import com.techsavvy.tshostelmanagement.ui.auth.LoginScreen


@Composable
fun NavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Screens.Login.route
    ) {

        composable(Screens.Login.route) {
            LoginScreen(navController, hiltViewModel())
        }

        // Admin Screens
        composable(Screens.Admin.Home.route){
            AdminHomeScreen(navController)
        }
        composable(Screens.Admin.Blocks.route){
            BlocksScreen()
        }
        composable(Screens.Admin.Floors.route){
            FloorsScreen()
        }
        composable(Screens.Admin.Rooms.route){
            RoomsScreen()
        }
        composable(Screens.Admin.Hostellers.route){
            HostellersScreen()
        }
        composable(Screens.Admin.Staff.route){
            StaffScreen()
        }
        composable(Screens.Admin.Settings.route){
            SettingsScreen(navController)
        }
        composable(Screens.Admin.AddBlock.route){
            AddBlockScreen()
        }
        composable(Screens.Admin.AddFloor.route){
            AddFloorScreen()
        }
        composable(Screens.Admin.AddRoom.route){
            AddRoomScreen()
        }
    }
}