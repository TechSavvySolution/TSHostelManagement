package com.techsavvy.tshostelmanagement.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.techsavvy.tshostelmanagement.data.models.ChatMessage
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: FirestoreRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _currentUser = MutableStateFlow<com.techsavvy.tshostelmanagement.data.models.User?>(null)
    val currentUser = _currentUser.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val uid = auth.uid ?: return@launch
            _currentUser.value = repository.getUser(uid)
        }
    }

    fun loadMessages(complaintId: String) {
        viewModelScope.launch {
            repository.getMessages(complaintId).collect { msgs ->
                _messages.value = msgs
            }
        }
    }

    fun sendMessage(complaintId: String, text: String) {
        val user = _currentUser.value ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            val message = ChatMessage(
                senderId = user.uid,
                senderName = user.name,
                senderRole = user.role.name,
                message = text.trim(),
                timestamp = System.currentTimeMillis()
            )
            repository.sendMessage(complaintId, message)
        }
    }
}
