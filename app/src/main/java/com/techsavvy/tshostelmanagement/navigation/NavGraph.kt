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
import com.techsavvy.tshostelmanagement.ui.admin.hostelers.AssignHostellerScreen as AdminAssignHostellerScreen
import com.techsavvy.tshostelmanagement.ui.admin.hostelers.HostellersScreen
import com.techsavvy.tshostelmanagement.ui.admin.infrastructure.* // Import all infrastructure screens
import com.techsavvy.tshostelmanagement.ui.admin.profile.ProfileScreen
import com.techsavvy.tshostelmanagement.ui.admin.reports.ReportsScreen
import com.techsavvy.tshostelmanagement.ui.admin.settings.SettingsScreen
import com.techsavvy.tshostelmanagement.ui.admin.staff.StaffScreen
import com.techsavvy.tshostelmanagement.ui.auth.AuthViewModel
import com.techsavvy.tshostelmanagement.ui.auth.LoginScreen
import com.techsavvy.tshostelmanagement.ui.auth.RegisterUserScreen
import com.techsavvy.tshostelmanagement.ui.admin.infrastructure.InfrastructureViewModel

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Login.route
    ) {
        composable(Screens.Login.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                navController = navController,
                viewModel = authViewModel
            )
        }
        adminGraph(navController)
    }
}

fun NavGraphBuilder.adminGraph(navController: NavController) {
    navigation(startDestination = Screens.Admin.Home.route, route = "admin_graph") {
        composable(Screens.Admin.Home.route) { AdminHomeScreen(navController) }
        composable(Screens.Admin.Infrastructure.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("admin_graph") }
            val viewModel: InfrastructureViewModel = hiltViewModel(parentEntry)
            InfrastructureScreen(navController, viewModel)
        }
        composable(Screens.Admin.AddBlock.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("admin_graph") }
            val viewModel: InfrastructureViewModel = hiltViewModel(parentEntry)
            AddBlockScreen(navController, viewModel)
        }
        composable(Screens.Admin.AddFloor.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("admin_graph") }
            val viewModel: InfrastructureViewModel = hiltViewModel(parentEntry)
            AddFloorScreen(navController, viewModel)
        }
        composable(Screens.Admin.AddRoom.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("admin_graph") }
            val viewModel: InfrastructureViewModel = hiltViewModel(parentEntry)
            AddRoomScreen(navController, viewModel)
        }
        composable(
            route = "${Screens.Admin.EditBlock.route}/{blockId}",
            arguments = listOf(navArgument("blockId") { type = NavType.StringType })
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("admin_graph") }
            val viewModel: InfrastructureViewModel = hiltViewModel(parentEntry)
            EditBlockScreen(
                navController = navController,
                viewModel = viewModel,
                blockId = backStackEntry.arguments?.getString("blockId")
            )
        }
        composable(
            route = "${Screens.Admin.EditFloor.route}/{floorId}",
            arguments = listOf(navArgument("floorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("admin_graph") }
            val viewModel: InfrastructureViewModel = hiltViewModel(parentEntry)
            EditFloorScreen(
                navController = navController,
                viewModel = viewModel,
                floorId = backStackEntry.arguments?.getString("floorId")
            )
        }
        composable(
            route = "${Screens.Admin.EditRoom.route}/{roomId}",
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("admin_graph") }
            val viewModel: InfrastructureViewModel = hiltViewModel(parentEntry)
            EditRoomScreen(
                navController = navController,
                viewModel = viewModel,
                roomId = backStackEntry.arguments?.getString("roomId")
            )
        }
        composable(
            route = "${Screens.Admin.DetailsBlock.route}/{blockId}",
            arguments = listOf(navArgument("blockId") { type = NavType.StringType })
        ) { backStackEntry ->
            DetailsBlockScreen(
                navController = navController,
                blockId = backStackEntry.arguments?.getString("blockId")
            )
        }
        composable(
            route = "${Screens.Admin.DetailsFloor.route}/{floorId}",
            arguments = listOf(navArgument("floorId") { type = NavType.StringType })
        ) { backStackEntry ->
            DetailsFloorScreen(
                navController = navController,
                floorId = backStackEntry.arguments?.getString("floorId")
            )
        }
        composable(
            route = "${Screens.Admin.DetailsRoom.route}/{roomId}",
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            DetailsRoomScreen(
                navController = navController,
                roomId = backStackEntry.arguments?.getString("roomId")
            )
        }
        composable(Screens.Admin.Hostellers.route) { HostellersScreen(navController) }
        composable(Screens.Admin.Staff.route) { StaffScreen() }
        composable(Screens.Admin.Complaints.route) { ComplaintsScreen() }
        composable(Screens.Admin.Fees.route) { FeesScreen() }
        composable(Screens.Admin.Reports.route) { ReportsScreen() }
        composable(Screens.Admin.Profile.route) { ProfileScreen() }
        composable(Screens.Admin.Settings.route) { SettingsScreen(navController) }

        composable("Screens.Auth.RegisterUser.route") {
            RegisterUserScreen(navController)
        }
        composable(
            route = "Screens.Admin.AssignHosteller.route/{uid}",
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            AssignHostellerScreen(navController = navController,viewModel= hiltViewModel())
        }
        
        composable("Screens.Admin.AddUser.route"){
            AddUserScreen(navController)
        }

        composable("Screens.Admin.AssignHosteller.route"){
            AdminAssignHostellerScreen(navController)
        }
    }
}