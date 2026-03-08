package com.techsavvy.tshostelmanagement.ui.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.techsavvy.tshostelmanagement.R
import com.techsavvy.tshostelmanagement.data.utils.Role
import com.techsavvy.tshostelmanagement.navigation.Screens
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, viewModel: AuthViewModel) {
    val authState by viewModel.authState.collectAsState()
    
    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_scale"
    )
    
    var isVisible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "fade_in"
    )

    LaunchedEffect(Unit) {
        isVisible = true
        // Extra delay to show off the splash screen animation if needed (min time)
        delay(1500)
    }

    LaunchedEffect(authState, isVisible) {
        if (isVisible) { // Only navigate after minimum visibility time
            delay(1500) // Ensure splash stays for at least 1.5s
            when (val state = authState) {
                is AuthState.Authenticated -> {
                    val destination = when (state.user?.role) {
                        Role.ADMIN -> Screens.Admin.Home.route
                        Role.STAFF -> Screens.Staff.Home.route
                        Role.HOSTELER -> Screens.Hosteler.Home.route
                        else -> Screens.Login.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screens.Splash.route) { inclusive = true }
                    }
                }
                is AuthState.Error -> {
                    navController.navigate(Screens.Login.route) {
                        popUpTo(Screens.Splash.route) { inclusive = true }
                    }
                }
                else -> {
                    // It can be Initial or Loading, wait...
                    // AuthViewModel checkUserExists() handles the transition out of Initial/Loading.
                    // If after 3 seconds it's still Initial/Loading, maybe force login screen:
                }
            }
        }
    }
    
    // Failsafe: if network is slow or user doesn't exist, we eventually timeout to login
    LaunchedEffect(authState) {
        delay(4000)
        if (authState !is AuthState.Authenticated) {
            navController.navigate(Screens.Login.route) {
                popUpTo(Screens.Splash.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020617)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(alpha)
        ) {
            Image(
                painter = painterResource(id = R.drawable.hsmlogo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp)
                    .scale(scale)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "TS HOSTEL MANAGEMENT",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Smart & Secure Living",
                color = Color(0xFF7DD3FC),
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
        }
    }
}
