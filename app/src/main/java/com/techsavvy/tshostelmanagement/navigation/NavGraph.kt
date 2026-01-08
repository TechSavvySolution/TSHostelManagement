package com.techsavvy.tshostelmanagement.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.techsavvy.tshostelmanagement.ui.admin.complaints.ComplaintsScreen
import com.techsavvy.tshostelmanagement.ui.admin.fees.FeesScreen
import com.techsavvy.tshostelmanagement.ui.admin.home.AdminHomeScreen
import com.techsavvy.tshostelmanagement.ui.admin.hostelers.AddUserScreen
import com.techsavvy.tshostelmanagement.ui.admin.hostelers.AssignHostellerScreen
import com.techsavvy.tshostelmanagement.ui.admin.hostelers.HostellersScreen
import com.techsavvy.tshostelmanagement.ui.admin.infrastructure.*
import com.techsavvy.tshostelmanagement.ui.admin.profile.ProfileScreen
import com.techsavvy.tshostelmanagement.ui.admin.reports.ReportsScreen
import com.techsavvy.tshostelmanagement.ui.admin.settings.SettingsScreen
import com.techsavvy.tshostelmanagement.ui.admin.staff.StaffScreen
import com.techsavvy.tshostelmanagement.ui.admin.staff.AssignTaskScreen
import com.techsavvy.tshostelmanagement.ui.auth.AuthViewModel
import com.techsavvy.tshostelmanagement.ui.auth.LoginScreen
import com.techsavvy.tshostelmanagement.ui.auth.RegisterUserScreen
import com.techsavvy.tshostelmanagement.ui.admin.infrastructure.InfrastructureViewModel
import com.techsavvy.tshostelmanagement.ui.admin.staff.AddStaffScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screens.Login.route
    ) {
        composable(Screens.Login.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            LoginScreen(navController = navController, viewModel = authViewModel)
        }
        adminGraph(navController)
    }
}

fun NavGraphBuilder.adminGraph(navController: NavController) {
    navigation(startDestination = Screens.Admin.Home.route, route = "admin_graph") {
        composable(Screens.Admin.Home.route) { AdminHomeScreen(navController) }

        // --- Infrastructure Module ---
        composable(Screens.Admin.Infrastructure.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("admin_graph") }
            InfrastructureScreen(navController, hiltViewModel(parentEntry))
        }
        composable(Screens.Admin.AddBlock.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("admin_graph") }
            AddBlockScreen(navController, hiltViewModel(parentEntry))
        }
        composable(Screens.Admin.AddFloor.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("admin_graph") }
            AddFloorScreen(navController, hiltViewModel(parentEntry))
        }
        composable(Screens.Admin.AddRoom.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("admin_graph") }
            AddRoomScreen(navController, hiltViewModel(parentEntry))
        }

        // --- Staff Module ---
        composable(Screens.Admin.Staff.route) { StaffScreen(navController) }
        composable(Screens.Admin.AssignTask.route) { AssignTaskScreen(navController) }
        composable(Screens.Admin.AddStaff.route) {
            AddStaffScreen(navController = navController)
        }

        // --- Hostellers Module ---
        composable(Screens.Admin.Hostellers.route) { HostellersScreen(navController) }
        composable(Screens.Admin.AddUser.route) { AddUserScreen(navController) }
        composable(Screens.Admin.AssignHosteller.route) { AssignHostellerScreen(navController) }

        composable(
            route = "${Screens.Admin.AssignHosteller.route}/{uid}",
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) {
            AssignHostellerScreen(navController = navController)
        }

        // --- Other Screens ---
        composable(Screens.Admin.Complaints.route) { ComplaintsScreen() }
        composable(Screens.Admin.Fees.route) { FeesScreen() }
        composable(Screens.Admin.Reports.route) { ReportsScreen() }
        composable(Screens.Admin.Profile.route) { ProfileScreen() }
        composable(Screens.Admin.Settings.route) { SettingsScreen(navController) }
        composable(Screens.Auth.RegisterUser.route) { RegisterUserScreen(navController) }

        // --- Detail/Edit Routes ---
        composable("${Screens.Admin.DetailsBlock.route}/{blockId}") { backStackEntry ->
            DetailsBlockScreen(navController, backStackEntry.arguments?.getString("blockId"))
        }
        composable("${Screens.Admin.DetailsFloor.route}/{floorId}") { backStackEntry ->
            DetailsFloorScreen(navController, backStackEntry.arguments?.getString("floorId"))
        }
        composable("${Screens.Admin.DetailsRoom.route}/{roomId}") { backStackEntry ->
            DetailsRoomScreen(navController, backStackEntry.arguments?.getString("roomId"))
        }
    }
}