package com.techsavvy.tshostelmanagement.payment

import android.app.Activity
import android.content.Intent
import android.widget.Toast

/**
 * PaymentManager — the single entry point the UI calls to initiate a payment.
 *
 * Usage:
 *   PaymentManager.startUpiPayment(activity, requestCode, amount, semesterName)
 *
 * In the calling Activity/Fragment override onActivityResult and pass results to:
 *   PaymentManager.handleResult(requestCode, resultCode, amount, callback)
 *
 * To swap in a real gateway later, only this class and UpiPaymentService need changing.
 */
object PaymentManager {

    const val UPI_REQUEST_CODE = 7001

    fun startUpiPayment(
        activity: Activity,
        amount: Double,
        semesterName: String
    ) {
        val intent = UpiPaymentService.buildPaymentIntent(amount, semesterName)
        val isAvailable = UpiPaymentService.isUpiAvailable(intent, activity.packageManager)

        if (isAvailable) {
            val chooser = Intent.createChooser(intent, "Pay with UPI")
            activity.startActivityForResult(chooser, UPI_REQUEST_CODE)
        } else {
            Toast.makeText(
                activity,
                "No UPI apps found. Please install GPay, PhonePe, or Paytm.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun handleResult(
        requestCode: Int,
        resultCode: Int,
        amount: Double,
        onResult: (PaymentResult) -> Unit
    ) {
        if (requestCode == UPI_REQUEST_CODE) {
            onResult(PaymentResultHandler.handle(resultCode.toString(), amount))
        }
    }
}
