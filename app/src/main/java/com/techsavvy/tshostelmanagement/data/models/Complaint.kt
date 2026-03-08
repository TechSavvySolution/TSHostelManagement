package com.techsavvy.tshostelmanagement.data.models

import com.google.firebase.firestore.DocumentId

data class Complaint(
    @DocumentId val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val floor: String = "",
    val roomNo: String = "",
    val title: String = "",
    val subject: String = "",
    val message: String = "",
    val status: String = "Pending",
    val createdAt: Long = System.currentTimeMillis(),
    val assignedStaffUid: String? = null,
    val assignedStaffName: String? = null,
    val assignedStaffPhone: String? = null,
    val deleted: Boolean = false   // SOFT DELETE flag
)