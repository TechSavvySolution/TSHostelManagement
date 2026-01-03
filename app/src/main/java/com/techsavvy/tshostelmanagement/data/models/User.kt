package com.techsavvy.tshostelmanagement.data.models

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "HOSTELLER",
    val active: Boolean = false
)
