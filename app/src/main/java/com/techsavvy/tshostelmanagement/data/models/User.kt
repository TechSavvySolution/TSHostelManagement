package com.techsavvy.tshostelmanagement.data.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val pass: String = "",
    val phone: String = "",
    val role: String = "HOSTELLER",
    val active: Boolean = false,
    val createdAt: Long? = System.currentTimeMillis()
)
