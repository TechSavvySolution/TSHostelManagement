package com.techsavvy.tshostelmanagement.ui.admin.fees

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.techsavvy.tshostelmanagement.data.models.FeeRecord
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeesScreen(
    navController: NavController? = null,
    viewModel: AdminFeesViewModel = hiltViewModel()
) {
    val feeRecords by viewModel.feeRecords.collectAsState()
    val latestSetting by viewModel.latestSetting.collectAsState()
    val publishSuccess by viewModel.publishSuccess.collectAsState()
    val isPublishing by viewModel.isPublishing.collectAsState()
    val semesterName by viewModel.semesterName.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val dueDate by viewModel.dueDate.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(publishSuccess) {
        if (publishSuccess) {
            Toast.makeText(context, "Fee published to all hostelers!", Toast.LENGTH_SHORT).show()
            viewModel.resetPublishFlag()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    // Date picker
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day)
            viewModel.dueDate.value = calendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        containerColor = Color(0xFF010413),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Fees Management", color = Color.White, fontWeight = FontWeight.Bold)
                        if (latestSetting != null)
                            Text("Latest: ${latestSetting!!.semesterName}", color = Color.Gray, fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    if (navController != null) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Rounded.ArrowBack, "Back", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0F1E))
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Publish New Fee Card ---
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.04f))
                        .border(1.dp, Color(0xFF818CF8).copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Payment, null, tint = Color(0xFF818CF8), modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Publish Semester Fee", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = semesterName,
                        onValueChange = { viewModel.semesterName.value = it },
                        label = { Text("Semester Name", color = Color.Gray) },
                        placeholder = { Text("e.g. Semester 2 - 2025", color = Color.Gray.copy(0.4f)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = feeFieldColors()
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { viewModel.amount.value = it },
                        label = { Text("Amount (₹)", color = Color.Gray) },
                        placeholder = { Text("e.g. 25000", color = Color.Gray.copy(0.4f)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = feeFieldColors()
                    )
                    Spacer(Modifier.height(10.dp))

                    // Due Date Picker
                    OutlinedTextField(
                        value = if (dueDate > 0L)
                            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(dueDate))
                        else "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Due Date", color = Color.Gray) },
                        placeholder = { Text("Tap to select", color = Color.Gray.copy(0.4f)) },
                        trailingIcon = {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Icon(Icons.Rounded.DateRange, null, tint = Color(0xFF818CF8))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = feeFieldColors()
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.publishFee() },
                        enabled = !isPublishing,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            Modifier.fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFF818CF8))),
                                    RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPublishing) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Publish to All Hostelers", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // --- Records Header ---
            item {
                Text(
                    "Fee Records (${feeRecords.size})",
                    color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp
                )
            }

            if (feeRecords.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No fee records yet. Publish a semester fee above.", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            } else {
                items(feeRecords) { record ->
                    AdminFeeRecordCard(record = record, onMarkPaid = { viewModel.markAsPaid(record.id) })
                }
            }
        }
    }
}

@Composable
fun AdminFeeRecordCard(record: FeeRecord, onMarkPaid: () -> Unit) {
    val isPaid = record.status == "Paid"
    val statusColor = if (isPaid) Color(0xFF4ADE80) else Color(0xFFF87171)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(1.dp, statusColor.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(record.hostelerName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(record.semesterName, color = Color.Gray, fontSize = 12.sp)
            Text("₹${record.amount.toInt()}", color = Color.White.copy(0.8f), fontSize = 13.sp)
        }

        Column(horizontalAlignment = Alignment.End) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(record.status, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            if (!isPaid) {
                Spacer(Modifier.height(6.dp))
                TextButton(
                    onClick = onMarkPaid,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF4ADE80))
                ) {
                    Text("Mark Paid", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun feeFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = Color(0xFF6366F1).copy(alpha = 0.6f),
    unfocusedBorderColor = Color.White.copy(alpha = 0.1f),
    focusedContainerColor = Color.White.copy(alpha = 0.04f),
    unfocusedContainerColor = Color.White.copy(alpha = 0.02f),
    focusedLabelColor = Color(0xFF818CF8),
)
