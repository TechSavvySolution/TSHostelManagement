package com.techsavvy.tshostelmanagement.data.models

import com.google.firebase.firestore.DocumentId

data class ChatMessage(
    @DocumentId val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderRole: String = "",   // "ADMIN" | "STAFF" | "HOSTELER"
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
