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
import com.techsavvy.tshostelmanagement.ui.admin.hostellers.HostellersScreen
import com.techsavvy.tshostelmanagement.ui.admin.infrastructure.* // Import all infrastructure screens
import com.techsavvy.tshostelmanagement.ui.admin.profile.ProfileScreen
import com.techsavvy.tshostelmanagement.ui.admin.reports.ReportsScreen
import com.techsavvy.tshostelmanagement.ui.admin.settings.SettingsScreen
import com.techsavvy.tshostelmanagement.ui.admin.staff.StaffScreen
import com.techsavvy.tshostelmanagement.ui.auth.AuthViewModel
import com.techsavvy.tshostelmanagement.ui.auth.LoginScreen
import com.techsavvy.tshostelmanagement.ui.hostel.HostelViewModel

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
            val viewModel: HostelViewModel = hiltViewModel(parentEntry)
            InfrastructureScreen(navController, viewModel)
        }
        composable("add_block") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("admin_graph") }
            val viewModel: HostelViewModel = hiltViewModel(parentEntry)
            AddBlockScreen(navController, viewModel)
        }
        composable("add_floor") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("admin_graph") }
            val viewModel: HostelViewModel = hiltViewModel(parentEntry)
            AddFloorScreen(navController, viewModel)
        }
        composable("add_room") { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("admin_graph") }
            val viewModel: HostelViewModel = hiltViewModel(parentEntry)
            AddRoomScreen(navController, viewModel)
        }
        composable(
            route = "edit_block/{blockId}",
            arguments = listOf(navArgument("blockId") { type = NavType.StringType })
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("admin_graph") }
            val viewModel: HostelViewModel = hiltViewModel(parentEntry)
            EditBlockScreen(
                navController = navController,
                viewModel = viewModel,
                blockId = backStackEntry.arguments?.getString("blockId")
            )
        }
        composable(
            route = "edit_floor/{floorId}",
            arguments = listOf(navArgument("floorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("admin_graph") }
            val viewModel: HostelViewModel = hiltViewModel(parentEntry)
            EditFloorScreen(
                navController = navController,
                viewModel = viewModel,
                floorId = backStackEntry.arguments?.getString("floorId")
            )
        }
        composable(
            route = "edit_room/{roomId}",
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry("admin_graph") }
            val viewModel: HostelViewModel = hiltViewModel(parentEntry)
            EditRoomScreen(
                navController = navController,
                viewModel = viewModel,
                roomId = backStackEntry.arguments?.getString("roomId")
            )
        }
        composable(
            route = "details_block/{blockId}",
            arguments = listOf(navArgument("blockId") { type = NavType.StringType })
        ) { backStackEntry ->
            DetailsBlockScreen(
                navController = navController,
                blockId = backStackEntry.arguments?.getString("blockId")
            )
        }
        composable(
            route = "details_floor/{floorId}",
            arguments = listOf(navArgument("floorId") { type = NavType.StringType })
        ) { backStackEntry ->
            DetailsFloorScreen(
                navController = navController,
                floorId = backStackEntry.arguments?.getString("floorId")
            )
        }
        composable(
            route = "details_room/{roomId}",
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            DetailsRoomScreen(
                navController = navController,
                roomId = backStackEntry.arguments?.getString("roomId")
            )
        }
        composable(Screens.Admin.Hostellers.route) { HostellersScreen() }
        composable(Screens.Admin.Staff.route) { StaffScreen() }
        composable(Screens.Admin.Complaints.route) { ComplaintsScreen() }
        composable(Screens.Admin.Fees.route) { FeesScreen() }
        composable(Screens.Admin.Reports.route) { ReportsScreen() }
        composable(Screens.Admin.Profile.route) { ProfileScreen() }
        composable(Screens.Admin.Settings.route) { SettingsScreen(navController) }
    }
}
