package com.techsavvy.tshostelmanagement.ui.admin.infrastructure

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
internal fun getOutlinedTextFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.White.copy(alpha = 0.1f),
    unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
    focusedBorderColor = Color(0xFF4ADE80),
    unfocusedBorderColor = Color.Transparent,
    focusedLabelColor = Color.White.copy(alpha = 0.8f),
    unfocusedLabelColor = Color.White.copy(alpha = 0.6f),
    cursorColor = Color(0xFF4ADE80),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)
