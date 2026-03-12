package com.techsavvy.tshostelmanagement.ui.admin.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

data class DashboardStats(
    val totalHostelers: Int = 0,
    val totalStaff: Int = 0,
    val pendingComplaints: Int = 0,
    val availableRooms: Int = 0,
    val totalRooms: Int = 0,
    val resolvedComplaints: Int = 0,
    val inProgressComplaints: Int = 0,
    val totalFeeRecords: Int = 0,
    val paidFeeRecords: Int = 0,
    val unpaidFeeRecords: Int = 0,
    val razorpayPaidRecords: Int = 0,
    val manualPaidRecords: Int = 0
)

@HiltViewModel
class AdminHomeViewModel @Inject constructor(
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _stats = MutableStateFlow(DashboardStats())
    val stats = _stats.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        // Hostelers count
        repository.getHostelers().onEach { list ->
            _stats.value = _stats.value.copy(totalHostelers = list.size)
        }.launchIn(viewModelScope)

        // Staff count
        repository.getStaff().onEach { list ->
            _stats.value = _stats.value.copy(totalStaff = list.size)
        }.launchIn(viewModelScope)

        // Complaints breakdown
        repository.getAllComplaints().onEach { list ->
            _stats.value = _stats.value.copy(
                pendingComplaints = list.count { it.status == "Pending" },
                inProgressComplaints = list.count { it.status == "In-Progress" },
                resolvedComplaints = list.count { it.status == "Resolved" }
            )
        }.launchIn(viewModelScope)

        // Rooms (available vs total)
        repository.getAllRooms().onEach { list ->
            _stats.value = _stats.value.copy(
                totalRooms = list.size,
                availableRooms = list.count { !it.isOccupied }
            )
        }.launchIn(viewModelScope)

        // Fee records breakdown
        repository.getAllFeeRecords().onEach { list ->
            _stats.value = _stats.value.copy(
                totalFeeRecords = list.size,
                paidFeeRecords = list.count { it.status == "Paid" },
                unpaidFeeRecords = list.count { it.status == "Unpaid" },
                razorpayPaidRecords = list.count { it.status == "Paid" && it.paymentMethod == "UPI" },
                manualPaidRecords = list.count { it.status == "Paid" && it.paymentMethod != "UPI" }
            )
        }.launchIn(viewModelScope)
    }
}
