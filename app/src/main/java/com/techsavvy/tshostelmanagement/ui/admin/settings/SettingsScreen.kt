package com.techsavvy.tshostelmanagement.ui.admin.settings

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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

@Composable
fun SettingsScreen(navController: NavController) {
    var isDarkMode by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF010413))
    ) {
        GridBackground()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            item {
                Text(
                    text = "Settings",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
                ProfileHeader(navController)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Appearance Section
            item {
                SettingsCategory(title = "Appearance")
                GlassmorphicCard {
                    SettingItem(
                        icon = Icons.Rounded.WbSunny,
                        title = "Dark Mode",
                        subtitle = "Toggle between light and dark themes",
                        trailingContent = {
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { isDarkMode = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF4ADE80)
                                )
                            )
                        }
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    SettingItem(
                        icon = Icons.Rounded.Palette,
                        title = "Theme",
                        subtitle = "Customize app theme colors",
                        onClick = { /* TODO: Navigate to theme screen */ },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                                contentDescription = "Navigate",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Account Section
            item {
                SettingsCategory(title = "Account & Other")
                GlassmorphicCard {
                    SettingItem(
                        icon = Icons.Rounded.Notifications,
                        title = "Notifications",
                        subtitle = "Manage notification preferences",
                        onClick = { /* TODO: Navigate to notification settings */ },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                                contentDescription = "Navigate",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    SettingItem(
                        icon = Icons.Rounded.Security,
                        title = "Privacy & Security",
                        subtitle = "Manage your data and security",
                        onClick = { /* TODO: Navigate to privacy screen */ },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                                contentDescription = "Navigate",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    SettingItem(
                        icon = Icons.AutoMirrored.Rounded.Logout,
                        title = "Logout",
                        isDestructive = true,
                        onClick = {
                            navController.navigate(Screens.Login.route) {
                                popUpTo(Screens.Admin.Home.route) { inclusive = true }
                            }
                        }
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
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
fun ProfileHeader(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .border(2.dp, Brush.linearGradient(listOf(Color(0xFF4ADE80), Color(0xFF22D3EE))), CircleShape)
                .background(Color.White.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.AccountCircle, contentDescription = "Profile", tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(60.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Admin User", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(text = "admin@tshostel.com", color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate(Screens.Admin.Profile.route) },
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(text = "View Profile", color = Color.White)
        }
    }
}

@Composable
fun GlassmorphicCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .border(
                1.dp,
                Brush.linearGradient(listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0.1f))),
                RoundedCornerShape(24.dp)
            ),
        content = content
    )
}

@Composable
fun SettingsCategory(title: String) {
    Text(
        text = title,
        color = Color.White.copy(alpha = 0.7f),
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(bottom = 8.dp, top = 8.dp, start = 8.dp)
    )
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    isDestructive: Boolean = false,
    onClick: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    val titleColor = if (isDestructive) Color(0xFFF87171) else Color.White
    val iconColor = if (isDestructive) titleColor else Color.White
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick.invoke() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor.copy(alpha = 0.8f),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            Column {
                Text(text = title, color = titleColor, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = subtitle, color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp)
                }
            }
        }
        if (trailingContent != null) {
            Spacer(modifier = Modifier.size(16.dp))
            trailingContent()
        }
    }
}
