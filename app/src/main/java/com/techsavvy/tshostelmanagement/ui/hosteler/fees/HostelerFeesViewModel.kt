package com.techsavvy.tshostelmanagement.ui.hosteler.fees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.techsavvy.tshostelmanagement.data.models.FeeRecord
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HostelerFeesViewModel @Inject constructor(
    private val repository: FirestoreRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _feeRecords = MutableStateFlow<List<FeeRecord>>(emptyList())
    val feeRecords: StateFlow<List<FeeRecord>> = _feeRecords.asStateFlow()

    // Latest unpaid record for home screen badge
    val latestUnpaid: FeeRecord?
        get() = _feeRecords.value.firstOrNull { it.status == "Unpaid" }

    val feesStatusText: String
        get() = if (_feeRecords.value.isEmpty()) "No Records"
                else if (latestUnpaid != null) "Unpaid" else "Paid"

    init {
        loadFeeRecords()
    }

    private fun loadFeeRecords() {
        val uid = auth.uid ?: return
        viewModelScope.launch {
            repository.getFeeRecordsForHosteler(uid).collect {
                _feeRecords.value = it
            }
        }
    }

    fun markFeeAsPaidViaUpi(recordId: String, transactionId: String) {
        viewModelScope.launch {
            try {
                repository.markFeeAsPaidViaUpi(recordId, transactionId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
