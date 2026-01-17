package com.techsavvy.tshostelmanagement.data.models

import com.google.firebase.firestore.DocumentId

data class Complaint(
    @DocumentId val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",     // Added
    val userPhone: String = "",     // Added
    val floor: String = "",         // Added
    val roomNo: String = "",        // Added
    val title: String = "",         // Added
    val subject: String = "",
    val message: String = "",
    val status: String = "Pending",
    val createdAt: Long = System.currentTimeMillis(),
    val assignedStaffName: String? = null,
    val assignedStaffPhone: String? = null
)