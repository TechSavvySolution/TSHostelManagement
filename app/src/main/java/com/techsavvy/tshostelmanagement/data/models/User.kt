package com.techsavvy.tshostelmanagement.data.models

import com.techsavvy.tshostelmanagement.data.utils.Role

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val pass: String = "",
    val phone: String = "",
    val role: Role = Role.HOSTELER,
    val active: Boolean = false,
    val created_at: Long? = System.currentTimeMillis()
)
