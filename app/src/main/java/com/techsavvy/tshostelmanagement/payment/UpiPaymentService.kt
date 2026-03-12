package com.techsavvy.tshostelmanagement.payment

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

/**
 * UpiPaymentService — responsible for constructing a standard UPI deep-link intent.
 *
 * UPI URI spec: upi://pay?pa=<VPA>&pn=<Name>&tn=<Note>&am=<Amount>&cu=INR
 *
 * To replace with a real gateway, swap this class only — PaymentManager & UI stay unchanged.
 */
object UpiPaymentService {

    private const val MERCHANT_NAME    = "TS Hostel Management"
    private const val TRANSACTION_NOTE = "Hostel Fee Payment"

    /**
     * @param upiId  The receiving UPI VPA — comes from FeeRecord.upiId (set by admin at publish time).
     *               Falls back to "pay@upi" if blank (demo/test mode).
     */
    fun buildPaymentIntent(amount: Double, semesterName: String, upiId: String = "pay@upi"): Intent {
        val merchantId = upiId.ifBlank { "pay@upi" }
        val upiUri = Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", merchantId)
            .appendQueryParameter("pn", MERCHANT_NAME)
            .appendQueryParameter("tn", "$TRANSACTION_NOTE – $semesterName")
            .appendQueryParameter("am", String.format("%.2f", amount))
            .appendQueryParameter("cu", "INR")
            .build()

        return Intent(Intent.ACTION_VIEW, upiUri)
    }

    fun isUpiAvailable(intent: Intent, packageManager: PackageManager): Boolean {
        val activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return activities.isNotEmpty()
    }
}
