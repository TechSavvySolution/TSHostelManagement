package com.techsavvy.tshostelmanagement.ui.hosteler

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

private const val MAX_IMAGES = 3
private const val MAX_VIDEOS = 1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RaiseComplaintScreen(
    navController: NavController,
    viewModel: ComplaintViewModel = hiltViewModel()
) {
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val context = LocalContext.current

    val isUploading by viewModel.isUploading.collectAsState()
    val uploadError by viewModel.uploadError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show error in snackbar whenever uploadError changes
    LaunchedEffect(uploadError) {
        if (!uploadError.isNullOrBlank()) {
            snackbarHostState.showSnackbar(uploadError!!)
            viewModel.clearUploadError()
        }
    }

    // Multi-content picker (images)
    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        val imageUris = uris.filter { uri ->
            context.contentResolver.getType(uri)?.startsWith("image/") == true
        }
        val available = MAX_IMAGES - selectedUris.count {
            context.contentResolver.getType(it)?.startsWith("image/") == true
        }
        if (available <= 0) {
            Toast.makeText(context, "Maximum $MAX_IMAGES images allowed", Toast.LENGTH_SHORT).show()
        } else {
            selectedUris = selectedUris + imageUris.take(available)
        }
    }

    // Single video picker
    val videoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val hasVideo = selectedUris.any {
            context.contentResolver.getType(it)?.startsWith("video/") == true
        }
        if (hasVideo) {
            Toast.makeText(context, "Only $MAX_VIDEOS video allowed", Toast.LENGTH_SHORT).show()
        } else {
            selectedUris = selectedUris + uri
        }
    }

    Scaffold(
        containerColor = Color(0xFF010413),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF7F1D1D),
                    contentColor = Color.White
                )
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Raise New Complaint", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF010413))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                if (isUploading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        color = Color(0xFFF87171),
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                    Text(
                        "Uploading attachments...",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Button(
                    onClick = {
                        when {
                            subject.isBlank() || message.isBlank() -> {
                                Toast.makeText(context, "Subject and Message cannot be empty", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                viewModel.submitComplaint(subject, message, selectedUris,navController)
                            }
                        }
                    },
                    enabled = !isUploading,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF87171),
                        disabledContainerColor = Color(0xFFF87171).copy(alpha = 0.4f)
                    )
                ) {
                    Text("Register Complaint", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Subject field
            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Subject") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFF87171),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFFF87171),
                    unfocusedLabelColor = Color.Gray
                )
            )

            // Message field
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Actual Complaint (Message)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFF87171),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFFF87171),
                    unfocusedLabelColor = Color.Gray
                )
            )

            // ── Attach Media Section ──────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .padding(16.dp)
            ) {
                Text(
                    "Attachments (Optional)",
                    color = Color(0xFFF87171),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    "Up to 3 images or 1 video",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                // Thumbnail row for selected files
                if (selectedUris.isNotEmpty()) {
                    MediaThumbnailRow(
                        uris = selectedUris,
                        context = context,
                        onRemove = { uri ->
                            selectedUris = selectedUris.filter { it != uri }
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                }

                // Attachment action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val imageCount = selectedUris.count {
                        context.contentResolver.getType(it)?.startsWith("image/") == true
                    }
                    val hasVideo = selectedUris.any {
                        context.contentResolver.getType(it)?.startsWith("video/") == true
                    }

                    OutlinedButton(
                        onClick = { imagePicker.launch("image/*") },
                        enabled = imageCount < MAX_IMAGES && !isUploading,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFF87171).copy(alpha = 0.6f)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.AttachFile,
                            contentDescription = null,
                            tint = Color(0xFFF87171),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Photo (${imageCount}/$MAX_IMAGES)",
                            color = Color(0xFFF87171),
                            fontSize = 13.sp
                        )
                    }

                    OutlinedButton(
                        onClick = { videoPicker.launch("video/*") },
                        enabled = !hasVideo && !isUploading,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF22D3EE).copy(alpha = 0.6f)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = Color(0xFF22D3EE),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Video (${if (hasVideo) "1" else "0"}/$MAX_VIDEOS)",
                            color = Color(0xFF22D3EE),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun MediaThumbnailRow(
    uris: List<Uri>,
    context: android.content.Context,
    onRemove: (Uri) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        uris.forEach { uri ->
            val isVideo = context.contentResolver.getType(uri)?.startsWith("video/") == true
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                if (isVideo) {
                    // Video placeholder
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PlayCircle,
                            contentDescription = "Video",
                            tint = Color(0xFF22D3EE),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                } else {
                    AsyncImage(
                        model = uri,
                        contentDescription = "Attached image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Remove (✕) button
                IconButton(
                    onClick = { onRemove(uri) },
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(Color(0xFF1E1E2E))
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}