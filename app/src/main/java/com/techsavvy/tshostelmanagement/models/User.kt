package com.techsavvy.tshostelmanagement.models

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "HOSTELLER",
    val active: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
