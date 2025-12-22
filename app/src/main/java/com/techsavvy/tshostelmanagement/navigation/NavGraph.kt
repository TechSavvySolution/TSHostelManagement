package com.techsavvy.tshostelmanagement.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.techsavvy.tshostelmanagement.ui.auth.LoginScreen
import com.techsavvy.tshostelmanagement.ui.home.HomeScreen


@Composable
fun NavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(navController)
        }

        composable("home") {
            HomeScreen()
        }
    }
}
