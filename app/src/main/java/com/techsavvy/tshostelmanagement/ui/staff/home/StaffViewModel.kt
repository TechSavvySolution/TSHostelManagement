//package com.techsavvy.tshostelmanagement.ui.staff.profile
//
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.google.firebase.auth.FirebaseAuth
//import com.techsavvy.tshostelmanagement.data.models.User
//import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class StaffProfileViewModel @Inject constructor(
//    private val auth: FirebaseAuth,
//    private val repository: FirestoreRepository
//) : ViewModel() {
//
//    var userData by mutableStateOf<User?>(null)
//        private set
//
//    var isEditMode by mutableStateOf(false)
//        private set
//
//    // Edit Fields
//    var editName by mutableStateOf("")
//    var editPhone by mutableStateOf("")
//
//    init {
//        fetchProfile()
//    }
//
//    private fun fetchProfile() {
//        val uid = auth.currentUser?.uid ?: return
//        viewModelScope.launch {
//            val user = repository.getUser(uid)
//            userData = user
//            user?.let {
//                editName = it.name
//                editPhone = it.phone
//            }
//        }
//    }
//
//    fun toggleEditMode() {
//        isEditMode = !isEditMode
//    }
//
//    fun saveProfile() {
//        val uid = auth.currentUser?.uid ?: return
//        val currentUser = userData ?: return
//
//        viewModelScope.launch {
//            val updatedUser = currentUser.copy(
//                name = editName,
//                phone = editPhone
//            )
//            repository.saveUser(updatedUser)
//            userData = updatedUser
//            isEditMode = false
//        }
//    }
//}