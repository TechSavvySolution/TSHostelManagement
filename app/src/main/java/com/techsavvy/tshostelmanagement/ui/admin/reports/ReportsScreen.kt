package com.techsavvy.tshostelmanagement.ui.admin.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techsavvy.tshostelmanagement.ui.admin.home.AdminHomeViewModel

data class ReportStat(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: AdminHomeViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val context = LocalContext.current

    val reportStats = listOf(
        ReportStat("Total Students", stats.totalHostelers.toString(), Icons.Rounded.Group, Color(0xFF4ADE80)),
        ReportStat("Total Staff", stats.totalStaff.toString(), Icons.Rounded.People, Color(0xFFF87171)),
        ReportStat("Pending Complaints", stats.pendingComplaints.toString(), Icons.Rounded.Report, Color(0xFFFACC15)),
        ReportStat("Resolved Complaints", stats.resolvedComplaints.toString(), Icons.Rounded.CheckCircle, Color(0xFF22D3EE)),
        ReportStat("Available Rooms", stats.availableRooms.toString(), Icons.Rounded.Bed, Color(0xFFA78BFA)),
        ReportStat("Occupied Rooms", (stats.totalRooms - stats.availableRooms).toString(), Icons.Rounded.MeetingRoom, Color(0xFFFB923C)),
        ReportStat("Paid Fees", stats.paidFeeRecords.toString(), Icons.Rounded.Payment, Color(0xFF818CF8)),
        ReportStat("Unpaid Fees", stats.unpaidFeeRecords.toString(), Icons.Rounded.MoneyOff, Color(0xFFF43F5E))
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF010413),
        topBar = {
            TopAppBar(
                title = { Text("Reports Overview", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { exportToCsv(context, stats) },
                containerColor = Color(0xFF22D3EE),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Download, contentDescription = "Export CSV", tint = Color.Black)
                    Spacer(Modifier.width(8.dp))
                    Text("Export CSV", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Title
            item {
                Text(
                    text = "Live Hostel Analytics",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // --- 💳 Payments Overview ---
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.04f))
                        .border(
                            1.dp,
                            Brush.linearGradient(listOf(Color(0xFF22D3EE).copy(0.4f), Color(0xFF6366F1).copy(0.4f))),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Payment, null, tint = Color(0xFF22D3EE), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Payments Overview", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        PaymentMethodStat(label = "UPI", count = stats.razorpayPaidRecords, color = Color(0xFF4ADE80), emoji = "📲")
                        PaymentMethodStat(label = "Manual", count = stats.manualPaidRecords, color = Color(0xFF22D3EE), emoji = "✅")
                        PaymentMethodStat(label = "Unpaid", count = stats.unpaidFeeRecords, color = Color(0xFFF87171), emoji = "⏳")
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Analytics Grid
            item {
                androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                    columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    modifier = Modifier.height(((reportStats.size / 2 + reportStats.size % 2) * 160).dp)
                ) {
                    items(reportStats) { stat ->
                        ReportCard(stat)
                    }
                }
            }
        }
    }
}

@Composable
fun ReportCard(stat: ReportStat) {
    Box(
        modifier = Modifier
            .height(140.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(
                1.dp,
                Brush.linearGradient(listOf(stat.color.copy(alpha = 0.5f), Color.Transparent)),
                RoundedCornerShape(24.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(stat.color.copy(alpha = 0.1f))
                    .border(
                        1.dp,
                        stat.color.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = stat.icon,
                    contentDescription = stat.title,
                    tint = stat.color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stat.title,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stat.value,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun exportToCsv(context: Context, stats: com.techsavvy.tshostelmanagement.ui.admin.home.DashboardStats) {
    val csvData = StringBuilder()
    csvData.append("Report Category,Count\n")
    csvData.append("Total Students,${stats.totalHostelers}\n")
    csvData.append("Total Staff,${stats.totalStaff}\n")
    csvData.append("Pending Complaints,${stats.pendingComplaints}\n")
    csvData.append("Resolved Complaints,${stats.resolvedComplaints}\n")
    csvData.append("Available Rooms,${stats.availableRooms}\n")
    csvData.append("Occupied Rooms,${stats.totalRooms - stats.availableRooms}\n")
    csvData.append("Paid Fees (Total),${stats.paidFeeRecords}\n")
    csvData.append("Paid via Razorpay,${stats.razorpayPaidRecords}\n")
    csvData.append("Paid Manually,${stats.manualPaidRecords}\n")
    csvData.append("Unpaid Fees,${stats.unpaidFeeRecords}\n")

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_SUBJECT, "Hostel Reports CSV")
        putExtra(Intent.EXTRA_TEXT, csvData.toString())
    }
    context.startActivity(Intent.createChooser(intent, "Export Report..."))
}

@Composable
fun PaymentMethodStat(label: String, count: Int, color: Color, emoji: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 22.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            text = count.toString(),
            color = color,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
