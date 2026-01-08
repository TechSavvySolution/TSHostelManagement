package com.techsavvy.tshostelmanagement.data.models

import com.google.firebase.firestore.DocumentId

data class StaffTask(
    @DocumentId val id: String = "",
    val staffUid: String = "",
    val staffName: String = "",
    val taskTitle: String = "",
    val description: String = "",
    val status: String = "Pending", // e.g., Pending, In Progress, Completed
    val assignedAt: Long = System.currentTimeMillis()
)