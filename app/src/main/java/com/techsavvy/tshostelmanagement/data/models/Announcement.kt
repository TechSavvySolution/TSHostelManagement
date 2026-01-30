package com.techsavvy.tshostelmanagement.data.models

import com.google.firebase.firestore.DocumentId

data class Announcement(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val order: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)