package com.techsavvy.tshostelmanagement.ui.hosteler.fees

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.techsavvy.tshostelmanagement.data.models.FeeRecord
import com.techsavvy.tshostelmanagement.payment.PaymentManager
import com.techsavvy.tshostelmanagement.payment.PaymentResult
import com.techsavvy.tshostelmanagement.payment.UpiPaymentService
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
    val context = LocalContext.current

    // Pending payment state: the record currently being paid
    var pendingRecord by remember { mutableStateOf<FeeRecord?>(null) }
    // Payment result overlay state
    var paymentResult by remember { mutableStateOf<PaymentResult?>(null) }
    val upiLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        val record = pendingRecord ?: return@rememberLauncherForActivityResult

        val response = result.data?.getStringExtra("response") ?: ""

        val outcome = com.techsavvy.tshostelmanagement.payment.PaymentResultHandler
            .handle(response, record.amount)

        paymentResult = outcome

        if (outcome is PaymentResult.Success) {
            viewModel.markFeeAsPaidViaUpi(record.id, outcome.transactionId)
        }

        pendingRecord = null
    }

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
                    HostelerFeeRecordCard(
                        record = record,
                        onPayViaUpi = {
                            val intent = UpiPaymentService.buildPaymentIntent(
                                record.amount,
                                record.semesterName,
                                record.upiId
                            )

                            try {
                                pendingRecord = record
                                upiLauncher.launch(Intent.createChooser(intent, "Pay with UPI"))
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "No UPI apps installed.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
//                            val available = UpiPaymentService.isUpiAvailable(intent, context.packageManager)
//                            if (available) {
//                                pendingRecord = record
//                                upiLauncher.launch(
//                                    android.content.Intent.createChooser(intent, "Pay with UPI")
//                                )
//                            } else {
//                                Toast.makeText(
//                                    context,
//                                    "No UPI apps found. Please install GPay, PhonePe, or Paytm.",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HostelerFeeRecordCard(record: FeeRecord, onPayViaUpi: () -> Unit) {
    val isPaid = record.status == "Paid"
    val statusColor = if (isPaid) Color(0xFF4ADE80) else Color(0xFFF87171)
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val isUpiPaid = record.paymentMethod == "UPI"

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
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (isPaid) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isUpiPaid) Color(0xFF4ADE80).copy(alpha = 0.12f)
                                else Color.Gray.copy(alpha = 0.12f)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            if (isUpiPaid) "📲 UPI" else "✅ Manual",
                            color = if (isUpiPaid) Color(0xFF4ADE80) else Color.Gray,
                            fontSize = 10.sp, fontWeight = FontWeight.Bold
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(record.status, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
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

        // Transaction ID for UPI paid records
        if (isPaid && isUpiPaid && record.transactionId.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                "TXNID: ${record.transactionId}",
                color = Color(0xFF4ADE80).copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }

        // PAY VIA UPI button for unpaid records
        if (!isPaid) {
            Spacer(Modifier.height(14.dp))

            // UPI info row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.AccountBalance, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Payment Method", color = Color.Gray, fontSize = 12.sp)
                Spacer(Modifier.weight(1f))
                Text("UPI Payment", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = onPayViaUpi,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    Modifier.fillMaxSize()
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFF22C55E), Color(0xFF16A34A))),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📲", fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Pay ₹${record.amount.toInt()} via UPI",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Full-screen payment result overlay — shown after the user returns from the UPI app.
 */
@Composable
fun PaymentResultScreen(result: PaymentResult, onDismiss: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    val isSuccess = result is PaymentResult.Success
    val isCancelled = result is PaymentResult.Cancelled

    val statusColor = when {
        isSuccess -> Color(0xFF4ADE80)
        isCancelled -> Color(0xFFFACC15)
        else -> Color(0xFFF87171)
    }
    val emoji = when {
        isSuccess -> "🎉"
        isCancelled -> "⚠️"
        else -> "❌"
    }
    val title = when {
        isSuccess -> "Payment Successful"
        isCancelled -> "Payment Cancelled"
        else -> "Payment Failed"
    }
    val subtitle = when {
        isSuccess -> "Your fee has been recorded."
        isCancelled -> "You cancelled the UPI payment."
        else -> (result as? PaymentResult.Failure)?.reason ?: "Something went wrong."
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF010413)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status icon circle
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.12f))
                    .border(2.dp, statusColor.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 40.sp)
            }

            Spacer(Modifier.height(24.dp))

            Text(title, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(6.dp))
            Text(subtitle, color = Color.Gray, fontSize = 14.sp, textAlign = TextAlign.Center)

            Spacer(Modifier.height(32.dp))

            // Result details card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (result is PaymentResult.Success) {
                    PaymentDetailRow("Amount Paid", "₹${result.amount.toInt()}")
                    PaymentDetailRow("Transaction ID", result.transactionId)
                    PaymentDetailRow("Timestamp", dateFormat.format(Date(result.timestamp)))
                    PaymentDetailRow("Payment Method", "UPI")
                    PaymentDetailRow("Status", "✅ PAID")
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    Modifier.fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                if (isSuccess)
                                    listOf(Color(0xFF22C55E), Color(0xFF16A34A))
                                else
                                    listOf(Color(0xFF6366F1), Color(0xFF818CF8))
                            ),
                            RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (isSuccess) "Back to My Fees" else "Try Again",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentDetailRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
