package com.techsavvy.tshostelmanagement.ui.admin.messmenu

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.techsavvy.tshostelmanagement.data.models.DayMenu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMessMenuScreen(
    navController: NavController,
    viewModel: MessMenuViewModel = hiltViewModel()
) {
    val menu by viewModel.menu.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            Toast.makeText(context, "Menu saved successfully!", Toast.LENGTH_SHORT).show()
            viewModel.resetSaveFlag()
        }
    }

    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val dayMenus = listOf(
        menu.monday, menu.tuesday, menu.wednesday,
        menu.thursday, menu.friday, menu.saturday, menu.sunday
    )
    val dayColors = listOf(
        Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFF22D3EE),
        Color(0xFF4ADE80), Color(0xFFFACC15), Color(0xFFF87171), Color(0xFFA78BFA)
    )

    Scaffold(
        containerColor = Color(0xFF010413),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Mess Menu", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Weekly Meal Schedule", color = Color.Gray, fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0F1E))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.saveMenu() },
                containerColor = Color(0xFF6366F1),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Check, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Save Menu", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            days.forEachIndexed { index, day ->
                DayMenuCard(
                    day = day,
                    dayMenu = dayMenus[index],
                    accentColor = dayColors[index],
                    onMealChange = { meal, value -> viewModel.updateMeal(day, meal, value) }
                )
            }

            Spacer(Modifier.height(80.dp)) // Space for FAB
        }
    }
}

@Composable
fun DayMenuCard(
    day: String,
    dayMenu: DayMenu,
    accentColor: Color,
    onMealChange: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        // Day header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(accentColor, RoundedCornerShape(50))
            )
            Spacer(Modifier.width(10.dp))
            Text(day, color = accentColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(Modifier.height(14.dp))

        val meals = listOf(
            "🌅 Breakfast" to dayMenu.breakfast,
            "☀️ Lunch" to dayMenu.lunch,
            "🌙 Dinner" to dayMenu.dinner
        )
        val mealKeys = listOf("Breakfast", "Lunch", "Dinner")
        val mealColors = listOf(
            Color(0xFFF97316), // Breakfast - Orange
            Color(0xFFEAB308), // Lunch - Yellow
            Color(0xFF3B82F6)  // Dinner - Blue
        )

        meals.forEachIndexed { i, (label, value) ->
            MealField(
                label = label,
                value = value,
                accentColor = mealColors[i],
                onValueChange = { onMealChange(mealKeys[i], it) }
            )
            if (i < meals.size - 1) Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
fun MealField(label: String, value: String, accentColor: Color, onValueChange: (String) -> Unit) {
    Column {
        Text(label, color = accentColor.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter meal items...", color = Color.Gray.copy(alpha = 0.5f)) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = accentColor.copy(alpha = 0.6f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
                focusedContainerColor = accentColor.copy(alpha = 0.05f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.02f)
            )
        )
    }
}
