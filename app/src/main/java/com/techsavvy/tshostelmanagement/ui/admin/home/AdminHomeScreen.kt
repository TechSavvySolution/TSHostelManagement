package com.techsavvy.tshostelmanagement.ui.admin.home

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.techsavvy.tshostelmanagement.navigation.Screens
import com.techsavvy.tshostelmanagement.ui.theme.TSHostelManagementTheme

@Composable
fun AdminHomeScreen(navController: NavController) {
    TSHostelManagementTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF010413))
        ) {
            GridBackground()
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
            ) {
                item {
                    Header(navController = navController)
                    Spacer(modifier = Modifier.height(24.dp))
                    AnalyticsSection()
                    Spacer(modifier = Modifier.height(24.dp))
                    ModuleSection(navController = navController)
                }
            }
        }
    }
}

@Composable
fun GridBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "grid-bg")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = InfiniteRepeatableSpec(tween(5000, easing = LinearEasing)),
        label = "grid-offset"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val gridSize = 60.dp.toPx()
        val gridColor = Color.White.copy(alpha = 0.05f)
        val animatedOffset = offset * gridSize

        for (i in 0..size.width.toInt() / gridSize.toInt()) {
            drawLine(
                color = gridColor,
                start = Offset(i * gridSize, 0f),
                end = Offset(i * gridSize, size.height),
                strokeWidth = 1f
            )
        }
        for (i in 0..size.height.toInt() / gridSize.toInt()) {
            drawLine(
                color = gridColor,
                start = Offset(0f, i * gridSize + animatedOffset - gridSize),
                end = Offset(size.width, i * gridSize + animatedOffset - gridSize),
                strokeWidth = 1f
            )
        }
    }
}

@Composable
fun Header(navController: NavController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column {
            Text(text = "TS Hostel Mgt.", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
            Text(text = "Admin Dashboard", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        }
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.dp, Brush.linearGradient(listOf(Color.White.copy(alpha = 0.2f), Color.Transparent)), RoundedCornerShape(50.dp))
                .padding(horizontal = 8.dp)
        ) {
            IconButton(onClick = { navController.navigate(Screens.Admin.Settings.route) }) {
                Icon(imageVector = Icons.Rounded.Tune, contentDescription = "Settings", tint = Color.White.copy(alpha = 0.8f))
            }
            IconButton(onClick = { navController.navigate(Screens.Admin.Profile.route) }) {
                Icon(imageVector = Icons.Rounded.AccountCircle, contentDescription = "Profile", tint = Color.White.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
fun AnalyticsSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Live Analytics",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnalyticsCard(item = analyticsItems[0], modifier = Modifier.weight(1f))
            AnalyticsCard(item = analyticsItems[1], modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun AnalyticsCard(item: AnalyticsItem, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(150.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Brush.linearGradient(listOf(item.color.copy(alpha = 0.5f), Color.Transparent)), RoundedCornerShape(24.dp))
            .padding(16.dp),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(item.color.copy(alpha = 0.1f))
                    .border(1.dp, item.color.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = item.icon, contentDescription = item.title, tint = item.color, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = item.title, color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(text = item.value, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ModuleSection(navController: NavController) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Management Modules",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(16.dp))
        HeroModuleCard(module = adminModules.first(), onClick = { navController.navigate(adminModules.first().route) })
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            modifier = Modifier.height(380.dp) // Adjust height as needed
        ) {
            items(adminModules.drop(1)) { module ->
                ModuleGridItem(module = module, onClick = { navController.navigate(module.route) })
            }
        }
    }
}

@Composable
fun HeroModuleCard(module: AdminModule, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "hero-glow")
    val glow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = InfiniteRepeatableSpec(tween(1500, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .border(
                1.dp,
                Brush.linearGradient(listOf(module.color.copy(alpha = 0.7f * glow), module.color.copy(alpha = 0.2f))),
                RoundedCornerShape(24.dp)
            )
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(module.color.copy(alpha = 0.1f))
                    .border(1.dp, module.color.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = module.icon,
                    contentDescription = module.title,
                    tint = module.color,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = module.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(text = module.subtitle, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp, lineHeight = 18.sp)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = "Navigate",
            tint = module.color,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(24.dp)
        )
    }
}

@Composable
fun ModuleGridItem(module: AdminModule, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .height(180.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .border(
                1.dp,
                Brush.linearGradient(listOf(module.color.copy(alpha = 0.5f), Color.Transparent)),
                RoundedCornerShape(24.dp)
            )
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(module.color.copy(alpha = 0.1f))
                .border(1.dp, module.color.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = module.icon,
                contentDescription = module.title,
                tint = module.color,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(text = module.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
            contentDescription = "Navigate",
            tint = module.color,
            modifier = Modifier.size(16.dp)
        )
    }
}

data class AdminModule(
    val title: String,
    val subtitle: String,
    val route: String,
    val icon: ImageVector,
    val color: Color
)

data class AnalyticsItem(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val color: Color
)

val adminModules = listOf(
    AdminModule("Hostellers", "Manage student records and details", Screens.Admin.Hostellers.route, Icons.Rounded.Group, Color(0xFF4ADE80)),
    AdminModule("Infrastructure", "Manage buildings, rooms, and assets", Screens.Admin.Infrastructure.route, Icons.Rounded.Apartment, Color(0xFF22D3EE)),
    AdminModule("Staff", "Manage staff members and roles", Screens.Admin.Staff.route, Icons.Rounded.People, Color(0xFFF87171)),
    AdminModule("Complaints", "Track and resolve student complaints", Screens.Admin.Complaints.route, Icons.Rounded.Report, Color(0xFFFACC15)),
    AdminModule("Fees", "Manage fee payments and records", Screens.Admin.Fees.route, Icons.Rounded.Payment, Color(0xFF818CF8)),
    AdminModule("Reports", "Generate and view hostel reports", Screens.Admin.Reports.route, Icons.Rounded.Assessment, Color(0xFFA78BFA))
)

val analyticsItems = listOf(
    AnalyticsItem("Total Students", "0", Icons.Rounded.Group, Color(0xFF4ADE80)),
    AnalyticsItem("Available Rooms", "0", Icons.Rounded.Bed, Color(0xFFFACC15)),
)
