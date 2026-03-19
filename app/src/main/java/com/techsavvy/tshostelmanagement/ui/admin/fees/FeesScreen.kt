package com.techsavvy.tshostelmanagement.ui.admin.fees

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
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
    val feeRecords        by viewModel.feeRecords.collectAsState()
    val latestSetting     by viewModel.latestSetting.collectAsState()
    val publishSuccess    by viewModel.publishSuccess.collectAsState()
    val isPublishing      by viewModel.isPublishing.collectAsState()
    val semesterName      by viewModel.semesterName.collectAsState()
    val amount            by viewModel.amount.collectAsState()
    val startDate         by viewModel.startDate.collectAsState()
    val dueDate           by viewModel.dueDate.collectAsState()
    val errorMessage      by viewModel.errorMessage.collectAsState()
    val selectedIds       by viewModel.selectedRecordIds.collectAsState()
    val isDeleting        by viewModel.isDeleting.collectAsState()
    val upiId             by viewModel.upiId.collectAsState()
    val context           = LocalContext.current

    // Group records by semester
    val grouped = feeRecords.groupBy { it.semesterName }.toSortedMap()

    // Bulk-delete confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteModeSemester by remember { mutableStateOf<String?>(null) } // null = by selection

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

    // Date pickers
    val calendar = Calendar.getInstance()
    fun datePicker(onSet: (Long) -> Unit) = DatePickerDialog(
        context,
        { _, y, m, d ->
            calendar.set(y, m, d, 0, 0, 0)
            onSet(calendar.timeInMillis)
        },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color(0xFF0A0F1E),
            titleContentColor = Color.White,
            textContentColor = Color.Gray,
            title = { Text("Confirm Delete", fontWeight = FontWeight.Bold) },
            text = {
                val msg = if (deleteModeSemester != null)
                    "Delete ALL records for \"${deleteModeSemester}\"?\nThis cannot be undone."
                else
                    "Delete ${selectedIds.size} selected record(s)?\nThis cannot be undone."
                Text(msg)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (deleteModeSemester != null) {
                            viewModel.deleteAllForSemester(deleteModeSemester!!)
                        } else {
                            viewModel.deleteSelected()
                        }
                        showDeleteDialog = false
                        deleteModeSemester = null
                    }
                ) { Text("Delete", color = Color(0xFFF87171), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

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

            // ─── Publish New Fee Card ───────────────────────────────────────
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
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
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        shape = RoundedCornerShape(12.dp), colors = feeFieldColors()
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { viewModel.amount.value = it },
                        label = { Text("Amount (₹)", color = Color.Gray) },
                        placeholder = { Text("e.g. 25000", color = Color.Gray.copy(0.4f)) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        shape = RoundedCornerShape(12.dp), colors = feeFieldColors()
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = upiId,
                        onValueChange = { viewModel.upiId.value = it },
                        label = { Text("UPI ID (Receiving VPA)", color = Color.Gray) },
                        placeholder = { Text("e.g. college@oksbi", color = Color.Gray.copy(0.4f)) },
                        modifier = Modifier.fillMaxWidth(), singleLine = true,
                        leadingIcon = { Text("📲", fontSize = 16.sp, modifier = Modifier.padding(start = 4.dp)) },
                        shape = RoundedCornerShape(12.dp), colors = feeFieldColors()
                    )
                    Spacer(Modifier.height(10.dp))

                    // Fee Duration Row (Start → Due)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = if (startDate > 0L) SimpleDateFormat("dd MMM yy", Locale.getDefault()).format(Date(startDate)) else "",
                            onValueChange = {}, readOnly = true,
                            label = { Text("Start Date", color = Color.Gray) },
                            placeholder = { Text("Tap", color = Color.Gray.copy(0.4f)) },
                            trailingIcon = {
                                IconButton(onClick = { datePicker { viewModel.startDate.value = it }.show() }) {
                                    Icon(Icons.Rounded.DateRange, null, tint = Color(0xFF818CF8))
                                }
                            },
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = feeFieldColors()
                        )
                        OutlinedTextField(
                            value = if (dueDate > 0L) SimpleDateFormat("dd MMM yy", Locale.getDefault()).format(Date(dueDate)) else "",
                            onValueChange = {}, readOnly = true,
                            label = { Text("Due Date", color = Color.Gray) },
                            placeholder = { Text("Tap", color = Color.Gray.copy(0.4f)) },
                            trailingIcon = {
                                IconButton(onClick = { datePicker { viewModel.dueDate.value = it }.show() }) {
                                    Icon(Icons.Rounded.DateRange, null, tint = Color(0xFF818CF8))
                                }
                            },
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = feeFieldColors()
                        )
                    }

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
                            Modifier.fillMaxSize().background(
                                Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFF818CF8))),
                                RoundedCornerShape(14.dp)
                            ), contentAlignment = Alignment.Center
                        ) {
                            if (isPublishing) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            else Text("Publish to All Hostelers", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ─── Bulk Action Toolbar ────────────────────────────────────────
            if (feeRecords.isNotEmpty()) {
                item {
                    Row(
                        Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Fee Records (${feeRecords.size})",
                            color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )

                        // Select All
                        TextButton(onClick = {
                            if (selectedIds.size == feeRecords.size) viewModel.clearSelection()
                            else viewModel.selectAll()
                        }) {
                            Text(
                                if (selectedIds.size == feeRecords.size) "Deselect All" else "Select All",
                                color = Color(0xFF818CF8), fontSize = 12.sp
                            )
                        }

                        // Delete Selected
                        AnimatedVisibility(selectedIds.isNotEmpty()) {
                            IconButton(onClick = {
                                deleteModeSemester = null
                                showDeleteDialog = true
                            }) {
                                Icon(Icons.Rounded.DeleteSweep, "Delete Selected", tint = Color(0xFFF87171))
                            }
                        }
                    }
                }

                // Selected count chip
                if (selectedIds.isNotEmpty()) {
                    item {
                        Row(
                            Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF87171).copy(0.08f))
                                .border(1.dp, Color(0xFFF87171).copy(0.2f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.CheckBox, null, tint = Color(0xFFF87171), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("${selectedIds.size} selected", color = Color(0xFFF87171), fontSize = 13.sp, modifier = Modifier.weight(1f))
                            if (isDeleting) {
                                CircularProgressIndicator(color = Color(0xFFF87171), modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            } else {
                                TextButton(onClick = { viewModel.clearSelection() }) {
                                    Text("Clear", color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // ─── Records grouped by Semester ───────────────────────────
                grouped.forEach { (semester, records) ->
                    // Semester header row
                    item {
                        Spacer(Modifier.height(4.dp))
                        Row(
                            Modifier.fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF818CF8).copy(alpha = 0.08f))
                                .border(1.dp, Color(0xFF818CF8).copy(0.2f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.School, null, tint = Color(0xFF818CF8), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(semester, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                val sr = records.firstOrNull()
                                if (sr != null && sr.startDate > 0L && sr.dueDate > 0L) {
                                    val fmt = SimpleDateFormat("dd MMM yy", Locale.getDefault())
                                    Text(
                                        "${fmt.format(Date(sr.startDate))} → ${fmt.format(Date(sr.dueDate))}",
                                        color = Color(0xFF818CF8),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            val paid = records.count { it.status == "Paid" }
                            Text("$paid/${records.size} Paid", color = Color(0xFF4ADE80), fontSize = 12.sp)
                            Spacer(Modifier.width(8.dp))
                            // Select all in semester
                            IconButton(onClick = {
                                val semIds = records.map { it.id }.toSet()
                                val alreadyAllSelected = selectedIds.containsAll(semIds)
                                if (alreadyAllSelected) {
                                    semIds.forEach { id ->
                                        if (selectedIds.contains(id)) viewModel.toggleSelection(id)
                                    }
                                } else {
                                    semIds.forEach { id ->
                                        if (!selectedIds.contains(id)) viewModel.toggleSelection(id)
                                    }
                                }
                            }, modifier = Modifier.size(28.dp)) {
                                val semIds = records.map { it.id }.toSet()
                                val allSemSelected = selectedIds.containsAll(semIds) && semIds.isNotEmpty()
                                Icon(
                                    if (allSemSelected) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
                                    null,
                                    tint = if (allSemSelected) Color(0xFF818CF8) else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            // Delete whole semester
                            IconButton(onClick = {
                                deleteModeSemester = semester
                                showDeleteDialog = true
                            }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Rounded.DeleteForever, null, tint = Color(0xFFF87171), modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    // Records in semester
                    items(records) { record ->
                        AdminFeeRecordCard(
                            record = record,
                            isSelected = selectedIds.contains(record.id),
                            onSelectToggle = { viewModel.toggleSelection(record.id) },
                            onMarkPaid = { viewModel.markAsPaid(record.id) }
                        )
                    }
                }
            } else {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💰", fontSize = 40.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("No fee records yet.", color = Color.Gray)
                            Text("Publish a semester fee above.", color = Color.Gray.copy(0.6f), fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminFeeRecordCard(
    record: FeeRecord,
    isSelected: Boolean,
    onSelectToggle: () -> Unit,
    onMarkPaid: () -> Unit
) {
    val isPaid = record.status == "Paid"
    val statusColor = if (isPaid) Color(0xFF4ADE80) else Color(0xFFF87171)
    val isUpi = record.paymentMethod == "UPI"
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) Color(0xFF818CF8).copy(alpha = 0.07f)
                else Color.White.copy(alpha = 0.04f)
            )
            .border(
                1.dp,
                if (isSelected) Color(0xFF818CF8).copy(0.5f) else statusColor.copy(alpha = 0.25f),
                RoundedCornerShape(16.dp)
            )
            .clickable { onSelectToggle() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox
        Box(
            modifier = Modifier.size(24.dp).clip(CircleShape)
                .background(if (isSelected) Color(0xFF818CF8) else Color.White.copy(0.08f)),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
        }
        Spacer(Modifier.width(12.dp))
        // Avatar + info row
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            // Circular initial avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF4ADE80).copy(alpha = 0.12f))
                    .border(1.5.dp, Color(0xFF4ADE80).copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    record.hostelerName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    color = Color(0xFF4ADE80),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(12.dp))

            Column {
                Text(record.hostelerName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("₹${record.amount.toInt()}", color = Color.White.copy(0.7f), fontSize = 13.sp)

                // Transaction ID for UPI paid records
                if (isPaid && isUpi && record.transactionId.isNotEmpty()) {
                    Text("TXNID: ${record.transactionId}", color = Color(0xFF4ADE80).copy(0.7f), fontSize = 11.sp)
                }

                // Paid date or due date
                if (isPaid && record.paidAt != null) {
                    Text("Paid: ${dateFormat.format(Date(record.paidAt))}", color = Color.Gray, fontSize = 11.sp)
                } else if (record.dueDate > 0L) {
                    Text("Due: ${dateFormat.format(Date(record.dueDate))}", color = Color(0xFFF87171).copy(0.8f), fontSize = 11.sp)
                }
            }
        }  // end inner Row

        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (isPaid) {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(6.dp))
                        .background(if (isUpi) Color(0xFF4ADE80).copy(0.12f) else Color.Gray.copy(0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        if (isUpi) "📲 UPI" else "✅ Manual",
                        color = if (isUpi) Color(0xFF4ADE80) else Color.Gray,
                        fontSize = 10.sp, fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(record.status, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            if (!isPaid) {
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
