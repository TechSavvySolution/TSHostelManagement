package com.techsavvy.tshostelmanagement.ui.admin.settings

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    fun logout() {
        auth.signOut()
    }
}