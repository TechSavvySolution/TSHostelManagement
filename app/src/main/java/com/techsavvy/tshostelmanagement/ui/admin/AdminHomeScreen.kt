package com.techsavvy.tshostelmanagement.ui.admin

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.techsavvy.tshostelmanagement.navigation.Screens
import com.techsavvy.tshostelmanagement.ui.theme.TSHostelManagementTheme
import kotlinx.coroutines.delay

@Composable
fun AdminHomeScreen(navController: NavController) {
    TSHostelManagementTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF010413))
        ) {
            GridBackground()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
            ) {
                Header(navController = navController)
                Spacer(modifier = Modifier.height(24.dp))
                AnalyticsCarousel()
                Spacer(modifier = Modifier.height(24.dp))
                ModuleGrid(navController = navController)
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
                Icon(imageVector = Icons.Rounded.Settings, contentDescription = "Settings", tint = Color.White.copy(alpha = 0.8f))
            }
            IconButton(onClick = { navController.navigate(Screens.Admin.Profile.route) }) {
                Icon(imageVector = Icons.Rounded.Person, contentDescription = "Profile", tint = Color.White.copy(alpha = 0.8f))
            }
        }
    }
}

@Composable
fun AnalyticsCarousel() {
    Column {
        Text(
            text = "Live Analytics",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(analyticsItems) { item ->
                AnalyticsCard(item = item)
            }
        }
    }
}

@Composable
fun AnalyticsCard(item: AnalyticsItem) {
    Box(
        modifier = Modifier
            .size(width = 160.dp, height = 120.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Brush.linearGradient(listOf(item.color.copy(alpha = 0.4f), Color.Transparent)), RoundedCornerShape(24.dp))
            .padding(16.dp),
    ) {
        Column(modifier=Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(item.color.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                 Icon(imageVector = item.icon, contentDescription = item.title, tint = item.color, modifier = Modifier.size(18.dp))
                 Text(text = item.title, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            }
             Text(text = item.value, color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ModuleGrid(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Management Modules",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(adminModules) { index, module ->
                val animatable = remember { Animatable(0f) }
                 LaunchedEffect(module) {
                    delay(100L * (index % 3))
                    animatable.animateTo(1f, tween(600))
                }
                ModuleCard(module = module, animation = animatable.value, onClick = { navController.navigate(module.route) })
            }
        }
    }
}

@Composable
fun ModuleCard(module: AdminModule, animation: Float, onClick: () -> Unit) {
    val color = module.color
    Column(
        modifier = Modifier
            .graphicsLayer {
                scaleY = animation
                alpha = animation
            }
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(color.copy(alpha = 0.15f), Color.Transparent),
                        radius = 150f
                    )
                )
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.dp, Brush.linearGradient(listOf(color.copy(alpha = 0.4f), Color.Transparent)), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = module.icon, contentDescription = null, tint = color.copy(alpha = 0.3f), modifier = Modifier.size(44.dp).offset(2.dp, 2.dp))
            Icon(imageVector = module.icon, contentDescription = module.title, tint = color, modifier = Modifier.size(44.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = module.title, color = Color.White.copy(alpha = 0.9f), fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, textAlign = TextAlign.Center)
    }
}

data class AdminModule(
    val title: String,
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
    AdminModule("Infrastructure", Screens.Admin.Infrastructure.route, Icons.Rounded.Apartment, Color(0xFF22D3EE)),
    AdminModule("Hostellers", Screens.Admin.Hostellers.route, Icons.Rounded.Group, Color(0xFF4ADE80)),
    AdminModule("Staff", Screens.Admin.Staff.route, Icons.Rounded.People, Color(0xFFF87171)),
    AdminModule("Complaints", Screens.Admin.Complaints.route, Icons.Rounded.Report, Color(0xFFFACC15)),
    AdminModule("Fees", Screens.Admin.Fees.route, Icons.Rounded.Payment, Color(0xFF818CF8)),
    AdminModule("Reports", Screens.Admin.Reports.route, Icons.Rounded.Assessment, Color(0xFFA78BFA))
)

val analyticsItems = listOf(
    AnalyticsItem("Hostellers", "0", Icons.Rounded.Group, Color(0xFF4ADE80)),
    AnalyticsItem("Buildings", "0", Icons.Rounded.Apartment, Color(0xFF22D3EE)),
    AnalyticsItem("Rooms", "0", Icons.Rounded.Bed, Color(0xFFFACC15)),
    AnalyticsItem("Staff", "0", Icons.Rounded.People, Color(0xFFF87171))
)
