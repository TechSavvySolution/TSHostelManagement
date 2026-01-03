package com.techsavvy.tshostelmanagement.ui.auth

import com.techsavvy.tshostelmanagement.data.models.User

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: User?) : AuthState()
    data class Error(val message: String) : AuthState()
}
