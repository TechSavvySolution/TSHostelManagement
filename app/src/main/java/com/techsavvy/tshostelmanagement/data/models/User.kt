package com.techsavvy.tshostelmanagement.data.models

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "hosteller",
    val active: Boolean = true
)