package com.techsavvy.tshostelmanagement.ui.hosteler.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.techsavvy.tshostelmanagement.data.models.User
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HostelerViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    // Mock Data for Dashboard
    val currentDate: String = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(Date())
    val feesStatus = "Paid" // Mock status: Paid, Pending, Overdue

    // Mock Mess Menu
    val messMenu = mapOf(
        "Breakfast" to "Aloo Paratha & Curd",
        "Lunch" to "Rice, Dal, Mixed Veg, Chapati",
        "Dinner" to "Fried Rice & Manchurian"
    )

    init {
        fetchCurrentUser()
    }

    private fun fetchCurrentUser() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            viewModelScope.launch {
                val user = repository.getUser(uid)
                _currentUser.value = user
            }
        }
    }
}