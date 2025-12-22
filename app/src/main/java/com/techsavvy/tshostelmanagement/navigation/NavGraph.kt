package com.techsavvy.tshostelmanagement.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.techsavvy.tshostelmanagement.ui.admin.home.HomeScreen
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
        // ALL HOMESCREENS FROM ALL ROLES
        composable(Screens.Admin.Home.route){
            HomeScreen(navController,hiltViewModel())
        }
    }
}
