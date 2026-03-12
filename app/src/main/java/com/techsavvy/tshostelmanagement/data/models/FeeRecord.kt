package com.techsavvy.tshostelmanagement.data.models

import com.google.firebase.firestore.DocumentId

data class FeeRecord(
    @DocumentId val id: String = "",
    val hostelerUid: String = "",
    val hostelerName: String = "",
    val semesterName: String = "",
    val amount: Double = 0.0,
    val status: String = "Unpaid",          // "Paid" | "Unpaid"
    val paidAt: Long? = null,
    val startDate: Long = 0L,
    val dueDate: Long = 0L,
    val upiId: String = "",                 // UPI VPA to pay to
    val paymentMethod: String = "Manual",   // "UPI" | "Manual"
    val transactionId: String = ""          // e.g. DEMO-1710245632 for UPI
)
