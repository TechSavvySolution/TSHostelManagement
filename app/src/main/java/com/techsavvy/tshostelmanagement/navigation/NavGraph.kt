package com.techsavvy.tshostelmanagement.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.techsavvy.tshostelmanagement.ui.admin.complaints.ComplaintsScreen
import com.techsavvy.tshostelmanagement.ui.admin.fees.FeesScreen
import com.techsavvy.tshostelmanagement.ui.admin.home.AdminHomeScreen
import com.techsavvy.tshostelmanagement.ui.admin.hostellers.HostellersScreen
import com.techsavvy.tshostelmanagement.ui.admin.infrastructure.AddBlockScreen
import com.techsavvy.tshostelmanagement.ui.admin.infrastructure.AddFloorScreen
import com.techsavvy.tshostelmanagement.ui.admin.infrastructure.AddRoomScreen
import com.techsavvy.tshostelmanagement.ui.admin.infrastructure.EditBlockScreen
import com.techsavvy.tshostelmanagement.ui.admin.infrastructure.EditFloorScreen
import com.techsavvy.tshostelmanagement.ui.admin.infrastructure.EditRoomScreen
import com.techsavvy.tshostelmanagement.ui.admin.infrastructure.InfrastructureScreen
import com.techsavvy.tshostelmanagement.ui.admin.infrastructure.InfrastructureViewModel
import com.techsavvy.tshostelmanagement.ui.admin.profile.ProfileScreen
import com.techsavvy.tshostelmanagement.ui.admin.reports.ReportsScreen
import com.techsavvy.tshostelmanagement.ui.admin.settings.SettingsScreen
import com.techsavvy.tshostelmanagement.ui.admin.staff.StaffScreen
import com.techsavvy.tshostelmanagement.ui.auth.AuthViewModel
import com.techsavvy.tshostelmanagement.ui.auth.LoginScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {
    val authViewModel: AuthViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screens.Login.route
    ) {

        composable(Screens.Login.route) {
            LoginScreen(
                navController = navController,
                viewModel = authViewModel
            )
        }

        adminNavGraph(navController)
    }
}

fun NavGraphBuilder.adminNavGraph(
    navController: NavController
) {
    composable(Screens.Admin.Home.route) {
        AdminHomeScreen(navController)
    }

    navigation(
        startDestination = Screens.Admin.Infrastructure.route,
        route = "admin_infrastructure_graph"
    ) {
        val infrastructureGraphRoute = "admin_infrastructure_graph"

        composable(Screens.Admin.Infrastructure.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(infrastructureGraphRoute) }
            val viewModel: InfrastructureViewModel = hiltViewModel(parentEntry)
            InfrastructureScreen(navController, viewModel)
        }
        composable(Screens.Admin.AddBlock.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(infrastructureGraphRoute) }
            val viewModel: InfrastructureViewModel = hiltViewModel(parentEntry)
            AddBlockScreen(navController, viewModel)
        }
        composable(Screens.Admin.AddFloor.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(infrastructureGraphRoute) }
            val viewModel: InfrastructureViewModel = hiltViewModel(parentEntry)
            AddFloorScreen(navController, viewModel)
        }
        composable(Screens.Admin.AddRoom.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(infrastructureGraphRoute) }
            val viewModel: InfrastructureViewModel = hiltViewModel(parentEntry)
            AddRoomScreen(navController, viewModel)
        }
        composable(Screens.Admin.EditBlock.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(infrastructureGraphRoute) }
            val viewModel: InfrastructureViewModel = hiltViewModel(parentEntry)
            val blockId = backStackEntry.arguments?.getString("blockId")
            EditBlockScreen(navController, viewModel, blockId)
        }
        composable(Screens.Admin.EditFloor.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(infrastructureGraphRoute) }
            val viewModel: InfrastructureViewModel = hiltViewModel(parentEntry)
            val floorId = backStackEntry.arguments?.getString("floorId")
            EditFloorScreen(navController, viewModel, floorId)
        }
        composable(Screens.Admin.EditRoom.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(infrastructureGraphRoute) }
            val viewModel: InfrastructureViewModel = hiltViewModel(parentEntry)
            val roomId = backStackEntry.arguments?.getString("roomId")
            EditRoomScreen(navController, viewModel, roomId)
        }
    }

    composable(Screens.Admin.Hostellers.route) {
        HostellersScreen()
    }

    composable(Screens.Admin.Staff.route) {
        StaffScreen()
    }

    composable(Screens.Admin.Complaints.route) {
        ComplaintsScreen()
    }

    composable(Screens.Admin.Fees.route) {
        FeesScreen()
    }

    composable(Screens.Admin.Reports.route) {
        ReportsScreen()
    }

    composable(Screens.Admin.Profile.route) {
        ProfileScreen()
    }

    composable(Screens.Admin.Settings.route) {
        SettingsScreen(navController)
    }
}
