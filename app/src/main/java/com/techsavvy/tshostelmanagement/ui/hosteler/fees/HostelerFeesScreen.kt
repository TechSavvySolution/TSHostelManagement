package com.techsavvy.tshostelmanagement.ui.hosteler.fees

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Warning
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
import com.techsavvy.tshostelmanagement.data.models.FeeRecord
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostelerFeesScreen(
    navController: NavController,
    viewModel: HostelerFeesViewModel = hiltViewModel()
) {
    val feeRecords by viewModel.feeRecords.collectAsState()
    val unpaidCount = feeRecords.count { it.status == "Unpaid" }

    Scaffold(
        containerColor = Color(0xFF010413),
        topBar = {
            TopAppBar(
                title = { Text("My Fees", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0A0F1E))
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Status Banner
            item {
                val hasUnpaid = unpaidCount > 0
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.horizontalGradient(
                                if (hasUnpaid)
                                    listOf(Color(0xFFF87171).copy(0.15f), Color(0xFFEF4444).copy(0.08f))
                                else
                                    listOf(Color(0xFF4ADE80).copy(0.12f), Color(0xFF22C55E).copy(0.06f))
                            )
                        )
                        .border(
                            1.dp,
                            if (hasUnpaid) Color(0xFFF87171).copy(0.4f) else Color(0xFF4ADE80).copy(0.4f),
                            RoundedCornerShape(18.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (hasUnpaid) Icons.Rounded.Warning else Icons.Rounded.CheckCircle,
                            null,
                            tint = if (hasUnpaid) Color(0xFFF87171) else Color(0xFF4ADE80),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                if (hasUnpaid) "Payment Due" else "All Fees Paid",
                                color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp
                            )
                            Text(
                                if (hasUnpaid) "$unpaidCount semester(s) pending" else "You're all clear!",
                                color = Color.Gray, fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            if (feeRecords.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💰", fontSize = 48.sp)
                            Spacer(Modifier.height(12.dp))
                            Text("No fee records yet", color = Color.Gray, fontSize = 16.sp)
                            Text("Admin has not published any fees", color = Color.Gray.copy(0.6f), fontSize = 13.sp)
                        }
                    }
                }
            } else {
                item {
                    Text(
                        "Fee History (${feeRecords.size})",
                        color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp
                    )
                }
                items(feeRecords) { record ->
                    HostelerFeeRecordCard(record = record)
                }
            }
        }
    }
}

@Composable
fun HostelerFeeRecordCard(record: FeeRecord) {
    val isPaid = record.status == "Paid"
    val statusColor = if (isPaid) Color(0xFF4ADE80) else Color(0xFFF87171)
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.04f))
            .border(1.dp, statusColor.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(record.semesterName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(statusColor.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(record.status, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Amount", color = Color.Gray, fontSize = 11.sp)
                Text("₹${record.amount.toInt()}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(if (isPaid) "Paid On" else "Due Date", color = Color.Gray, fontSize = 11.sp)
                Text(
                    if (isPaid && record.paidAt != null)
                        dateFormat.format(Date(record.paidAt))
                    else if (record.dueDate > 0L)
                        dateFormat.format(Date(record.dueDate))
                    else "—",
                    color = if (!isPaid) Color(0xFFF87171) else Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
