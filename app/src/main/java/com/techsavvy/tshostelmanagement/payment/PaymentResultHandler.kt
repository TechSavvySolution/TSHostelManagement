package com.techsavvy.tshostelmanagement.payment

import android.app.Activity

/**
 * PaymentResultHandler — interprets the raw Activity result code and data
 * returned after the user interacts with the UPI app and returns a PaymentResult.
 *
 * Result codes:
 *   RESULT_OK       → check the response string for SUCCESS / FAILURE
 *   RESULT_CANCELED → user backed out of UPI app
 *   other           → unexpected failure
 */
object PaymentResultHandler {

    fun handle(response: String, amount: Double): PaymentResult {

        if (response.isEmpty()) {
            return PaymentResult.Cancelled
        }

        val params = response.split("&")
            .mapNotNull {
                val parts = it.split("=")
                if (parts.size == 2) parts[0].lowercase() to parts[1] else null
            }.toMap()

        val status = params["status"]?.lowercase()
        val txnRef = params["txnref"] ?: params["approvalrefno"] ?: "DEMO-${System.currentTimeMillis()}"

        return when (status) {
            "success" -> PaymentResult.Success(
                transactionId = txnRef,
                amount = amount,
                timestamp = System.currentTimeMillis()
            )

            "failure" -> PaymentResult.Failure("Payment failed")

            else -> PaymentResult.Cancelled
        }
    }
}