package com.techsavvy.tshostelmanagement.ui.admin.hostellers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase
import com.techsavvy.tshostelmanagement.data.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HostellersViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            try {
                val result = db.collection("users").get().await()
                val userList = result.documents.mapNotNull { doc ->
                    val user = doc.toObject(User::class.java)
                    user?.copy(uid = doc.id)
                }
                _users.value = userList
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
