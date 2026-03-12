package com.techsavvy.tshostelmanagement.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.techsavvy.tshostelmanagement.ui.admin.announcements.AddEditAnnouncementScreen
import com.techsavvy.tshostelmanagement.ui.admin.complaints.AdminComplaintScreen
import com.techsavvy.tshostelmanagement.ui.admin.home.AdminHomeScreen
import com.techsavvy.tshostelmanagement.ui.admin.hostelers.AddUserScreen
import com.techsavvy.tshostelmanagement.ui.admin.hostelers.AssignHostellerScreen
import com.techsavvy.tshostelmanagement.ui.admin.hostelers.HostellersScreen
import com.techsavvy.tshostelmanagement.ui.admin.hostelers.EditUserScreen
import com.techsavvy.tshostelmanagement.ui.admin.infrastructure.*
import com.techsavvy.tshostelmanagement.ui.admin.profile.ProfileScreen
import com.techsavvy.tshostelmanagement.ui.admin.reports.ReportsScreen
import com.techsavvy.tshostelmanagement.ui.admin.settings.SettingsScreen
import com.techsavvy.tshostelmanagement.ui.admin.staff.StaffScreen
import com.techsavvy.tshostelmanagement.ui.admin.staff.AssignTaskScreen
import com.techsavvy.tshostelmanagement.ui.auth.AuthViewModel
import com.techsavvy.tshostelmanagement.ui.auth.LoginScreen
import com.techsavvy.tshostelmanagement.ui.auth.RegisterUserScreen
import com.techsavvy.tshostelmanagement.ui.auth.SplashScreen
import com.techsavvy.tshostelmanagement.ui.admin.staff.AddStaffScreen
import com.techsavvy.tshostelmanagement.ui.admin.staff.EditStaffScreen
import com.techsavvy.tshostelmanagement.ui.hosteler.HostelerComplaintsScreen
import com.techsavvy.tshostelmanagement.ui.hosteler.HostelerSettingsScreen
import com.techsavvy.tshostelmanagement.ui.hosteler.RaiseComplaintScreen
import com.techsavvy.tshostelmanagement.ui.hosteler.home.HostelerHomeScreen
import com.techsavvy.tshostelmanagement.ui.hosteler.profile.HostelerProfileScreen
import com.techsavvy.tshostelmanagement.ui.hosteler.RoommatesScreen

// ANNOUNCEMENT MODULE IMPORTS
import com.techsavvy.tshostelmanagement.ui.admin.announcements.AdminAnnouncementsScreen
import com.techsavvy.tshostelmanagement.ui.hosteler.home.HostelerAnnouncementsScreen

// STAFF MODULE IMPORTS
import com.techsavvy.tshostelmanagement.ui.staff.home.HomeScreen
import com.techsavvy.tshostelmanagement.ui.staff.complaints.StaffComplaintsScreen
import com.techsavvy.tshostelmanagement.ui.staff.complaints.StaffComplaintDetailsScreen
import com.techsavvy.tshostelmanagement.ui.staff.profile.StaffProfileScreen
import com.techsavvy.tshostelmanagement.ui.staff.settings.StaffSettingsScreen

// CHAT MODULE IMPORTS
import com.techsavvy.tshostelmanagement.ui.chat.ChatScreen
import com.techsavvy.tshostelmanagement.ui.hosteler.HostelerComplaintDetailScreen

// MESS MENU MODULE IMPORTS
import com.techsavvy.tshostelmanagement.ui.admin.messmenu.AdminMessMenuScreen

// FEES MODULE IMPORTS
import com.techsavvy.tshostelmanagement.ui.admin.fees.FeesScreen
import com.techsavvy.tshostelmanagement.ui.hosteler.fees.HostelerFeesScreen

// ABOUT SCREEN
import com.techsavvy.tshostelmanagement.ui.about.AboutScreen

@Composable
fun NavGraph(navController: NavHostController, p: Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screens.Splash.route,
        modifier = p
    ) {
        composable(Screens.Splash.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            SplashScreen(navController = navController, viewModel = authViewModel)
        }
        composable(Screens.Login.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            LoginScreen(navController = navController, viewModel = authViewModel)
        }
        adminGraph(navController)
        hostelerGraph(navController)
        staffGraph(navController)
    }
}

fun NavGraphBuilder.adminGraph(navController: NavController) {
    navigation(startDestination = Screens.Admin.Home.route, route = "admin_graph") {
        composable(Screens.Admin.Home.route) { AdminHomeScreen(navController) }
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
        composable(Screens.Admin.Staff.route) { StaffScreen(navController) }
        composable(Screens.Admin.AssignTask.route) { AssignTaskScreen(navController) }
        composable(Screens.Admin.AddStaff.route) { AddStaffScreen(navController = navController) }
        composable(
            route = "${Screens.Admin.EditStaff.route}/{uid}",
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            EditStaffScreen(navController, uid)
        }
        composable(Screens.Admin.Hostellers.route) { HostellersScreen(navController) }
        composable(Screens.Admin.AddUser.route) { AddUserScreen(navController) }
        composable(
            route = "${Screens.Admin.EditUser.route}/{uid}",
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            EditUserScreen(navController, uid)
        }
        composable(Screens.Admin.AssignHosteller.route) { AssignHostellerScreen(navController) }
        composable(
            route = "${Screens.Admin.AssignHosteller.route}/{uid}",
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) {
            AssignHostellerScreen(navController = navController)
        }

        // ADMIN ANNOUNCEMENTS
        composable(Screens.Admin.Announcements.route) { AdminAnnouncementsScreen(navController) }
        composable(Screens.Admin.AddAnnouncement.route) { AddEditAnnouncementScreen(navController) }
        composable("${Screens.Admin.EditAnnouncement.route}/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            AddEditAnnouncementScreen(navController, announcementId = id)
        }

        composable(Screens.Admin.Complaints.route) { AdminComplaintScreen(navController) }
        composable(Screens.Admin.MessMenu.route) { AdminMessMenuScreen(navController) }
        composable(Screens.Admin.Fees.route) { FeesScreen(navController = navController) }
        composable(Screens.Admin.Reports.route) { ReportsScreen() }
        composable(Screens.Admin.Profile.route) { ProfileScreen(navController) }
        composable(Screens.Admin.Settings.route) { SettingsScreen(navController) }
        composable(Screens.Admin.About.route) { AboutScreen(navController) }
        composable(Screens.Auth.RegisterUser.route) { RegisterUserScreen(navController) }
        composable("${Screens.Admin.DetailsBlock.route}/{blockId}") { backStackEntry ->
            DetailsBlockScreen(navController, backStackEntry.arguments?.getString("blockId"))
        }
        composable("${Screens.Admin.DetailsFloor.route}/{floorId}") { backStackEntry ->
            DetailsFloorScreen(navController, backStackEntry.arguments?.getString("floorId"))
        }
        composable("${Screens.Admin.DetailsRoom.route}/{roomId}") { backStackEntry ->
            DetailsRoomScreen(navController, backStackEntry.arguments?.getString("roomId"))
        }
        composable(
            route = "${Screens.Admin.EditBlock.route}/{blockId}",
            arguments = listOf(navArgument("blockId") { type = NavType.StringType })
        ) { backStackEntry ->
            val blockId = backStackEntry.arguments?.getString("blockId")
            EditBlockScreen(navController = navController, blockId = blockId)
        }
        composable(
            route = "${Screens.Admin.EditFloor.route}/{floorId}",
            arguments = listOf(navArgument("floorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val floorId = backStackEntry.arguments?.getString("floorId")
            EditFloorScreen(navController = navController, floorId = floorId)
        }
        composable(
            route = "${Screens.Admin.EditRoom.route}/{roomId}",
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")
            EditRoomScreen(navController = navController, roomId = roomId)
        }
    }
}

fun NavGraphBuilder.hostelerGraph(navController: NavController) {
    navigation(startDestination = Screens.Hosteler.Home.route, route = "hosteler_graph") {
        composable(Screens.Hosteler.Home.route) {
            HostelerHomeScreen(navController = navController)
        }
        composable(Screens.Hosteler.Complaints.route) {
            HostelerComplaintsScreen(navController)
        }
        composable(Screens.Hosteler.RaiseComplaint.route) {
            RaiseComplaintScreen(navController)
        }
        composable(Screens.Hosteler.Profile.route) {
            HostelerProfileScreen(navController = navController)
        }
        composable(Screens.Hosteler.Settings.route) {
            HostelerSettingsScreen(navController)
        }
        // HOSTELER ANNOUNCEMENTS
        composable(Screens.Hosteler.Announcements.route) {
            HostelerAnnouncementsScreen(navController)
        }
        // HOSTELER COMPLAINT DETAIL
        composable(
            route = "${Screens.Hosteler.ComplaintDetail.route}/{complaintId}",
            arguments = listOf(navArgument("complaintId") { type = NavType.StringType })
        ) { backStackEntry ->
            val complaintId = backStackEntry.arguments?.getString("complaintId")
            HostelerComplaintDetailScreen(navController, complaintId)
        }
        // HOSTELER CHAT
        composable(
            route = "${Screens.Hosteler.Chat.route}/{complaintId}",
            arguments = listOf(navArgument("complaintId") { type = NavType.StringType })
        ) { backStackEntry ->
            val complaintId = backStackEntry.arguments?.getString("complaintId")
            ChatScreen(navController = navController, complaintId = complaintId)
        }
        // HOSTELER FEES
        composable(Screens.Hosteler.Fees.route) {
            HostelerFeesScreen(navController)
        }
        // HOSTELER ABOUT
        composable(Screens.Hosteler.About.route) {
            AboutScreen(navController)
        }
        // HOSTELER ROOMMATES
        composable(Screens.Hosteler.Roommates.route) {
            RoommatesScreen(navController)
        }
    }
}

fun NavGraphBuilder.staffGraph(navController: NavController) {
    navigation(startDestination = Screens.Staff.Home.route, route = "staff_graph") {
        composable(Screens.Staff.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screens.Staff.Complaints.route) {
            StaffComplaintsScreen(navController)
        }
        composable(
            route = "${Screens.Staff.ComplaintDetails.route}/{complaintId}",
            arguments = listOf(navArgument("complaintId") { type = NavType.StringType })
        ) { backStackEntry ->
            val complaintId = backStackEntry.arguments?.getString("complaintId")
            StaffComplaintDetailsScreen(navController, complaintId)
        }
        composable(Screens.Staff.Profile.route) {
            StaffProfileScreen(navController = navController)
        }
        composable(Screens.Staff.Settings.route) {
            StaffSettingsScreen(navController = navController)
        }
        // STAFF CHAT (keyed by complaintId)
        composable(
            route = "${Screens.Staff.Chat.route}/{complaintId}",
            arguments = listOf(navArgument("complaintId") { type = NavType.StringType })
        ) { backStackEntry ->
            val complaintId = backStackEntry.arguments?.getString("complaintId")
            ChatScreen(navController = navController, complaintId = complaintId)
        }
        // STAFF ABOUT
        composable(Screens.Staff.About.route) {
            AboutScreen(navController)
        }
    }
}