package com.techsavvy.tshostelmanagement.payment

/**
 * PaymentResult — sealed class representing all possible payment outcomes.
 * Designed to be gateway-agnostic: just swap UpiPaymentService for a real gateway adapter later.
 */
sealed class PaymentResult {
    data class Success(
        val transactionId: String,
        val amount: Double,
        val timestamp: Long = System.currentTimeMillis()
    ) : PaymentResult()

    data class Failure(val reason: String) : PaymentResult()

    object Cancelled : PaymentResult()
}
