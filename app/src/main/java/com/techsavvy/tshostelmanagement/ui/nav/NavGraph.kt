package com.techsavvy.tshostelmanagement.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.techsavvy.tshostelmanagement.ui.auth.RegisterUserScreen
import com.techsavvy.tshostelmanagement.ui.hosteller.AssignHostellerScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "register_user") {
        composable("register_user") {
            RegisterUserScreen(navController)
        }
        composable(
            route = "assign_hosteller_screen/{uid}",
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""
            AssignHostellerScreen(navController = navController, uid = uid)
        }
    }
}
