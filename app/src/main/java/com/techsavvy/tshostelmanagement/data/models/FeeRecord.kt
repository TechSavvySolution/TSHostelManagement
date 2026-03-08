package com.techsavvy.tshostelmanagement.data.models

import com.google.firebase.firestore.DocumentId

data class FeeRecord(
    @DocumentId val id: String = "",
    val hostelerUid: String = "",
    val hostelerName: String = "",
    val semesterName: String = "",
    val amount: Double = 0.0,
    val status: String = "Unpaid",   // "Paid" | "Unpaid"
    val paidAt: Long? = null,
    val dueDate: Long = 0L
)
