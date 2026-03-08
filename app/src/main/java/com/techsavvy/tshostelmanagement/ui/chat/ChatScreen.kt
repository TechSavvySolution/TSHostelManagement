package com.techsavvy.tshostelmanagement.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.techsavvy.tshostelmanagement.data.models.ChatMessage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    complaintId: String?,
    complaintTitle: String = "Complaint Chat",
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(complaintId) {
        if (!complaintId.isNullOrBlank()) {
            viewModel.loadMessages(complaintId)
        }
    }

    // Auto-scroll to bottom when new message arrives
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFF010413),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Chat", color = Color.White, fontWeight = FontWeight.Bold)
                        Text(complaintTitle, color = Color.Gray, fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0F1E))
            )
        },
        bottomBar = {
            // Message Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0A0F1E))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Type a message...", color = Color.Gray) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.08f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(
                    onClick = {
                        if (complaintId != null && inputText.isNotBlank()) {
                            viewModel.sendMessage(complaintId, inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF22D3EE)))
                        )
                ) {
                    Icon(Icons.Rounded.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    ) { padding ->

        if (messages.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💬", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No messages yet", color = Color.Gray, fontSize = 16.sp)
                    Text("Start the conversation!", color = Color.Gray, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { msg ->
                    val isMe = msg.senderId == currentUser?.uid
                    ChatBubble(message = msg, isMe = isMe)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage, isMe: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        if (!isMe) {
            Text(
                text = "${message.senderName} (${message.senderRole.lowercase().replaceFirstChar { it.uppercase() }})",
                color = Color.Gray,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            )
        }
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isMe) 18.dp else 4.dp,
                        bottomEnd = if (isMe) 4.dp else 18.dp
                    )
                )
                .background(
                    if (isMe)
                        Brush.linearGradient(listOf(Color(0xFF22D3EE), Color(0xFF06B6D4)))
                    else
                        Brush.linearGradient(
                            listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.06f))
                        )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.message,
                color = if (isMe) Color.Black else Color.White,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
        // Timestamp
        Text(
            text = formatTime(message.timestamp),
            color = Color.Gray,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

private fun formatTime(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}
