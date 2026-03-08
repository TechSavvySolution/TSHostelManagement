package com.techsavvy.tshostelmanagement.ui.admin.fees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techsavvy.tshostelmanagement.data.models.FeeRecord
import com.techsavvy.tshostelmanagement.data.models.FeeSetting
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminFeesViewModel @Inject constructor(
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _feeRecords = MutableStateFlow<List<FeeRecord>>(emptyList())
    val feeRecords: StateFlow<List<FeeRecord>> = _feeRecords.asStateFlow()

    private val _latestSetting = MutableStateFlow<FeeSetting?>(null)
    val latestSetting: StateFlow<FeeSetting?> = _latestSetting.asStateFlow()

    private val _publishSuccess = MutableStateFlow(false)
    val publishSuccess = _publishSuccess.asStateFlow()

    private val _isPublishing = MutableStateFlow(false)
    val isPublishing = _isPublishing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Form fields
    val semesterName = MutableStateFlow("")
    val amount = MutableStateFlow("")
    val dueDate = MutableStateFlow(0L)

    init {
        loadFeeRecords()
        loadLatestSetting()
    }

    private fun loadFeeRecords() {
        viewModelScope.launch {
            repository.getAllFeeRecords().collect { _feeRecords.value = it }
        }
    }

    private fun loadLatestSetting() {
        viewModelScope.launch {
            repository.getLatestFeeSetting().collect { _latestSetting.value = it }
        }
    }

    fun publishFee() {
        val name = semesterName.value.trim()
        val amt = amount.value.toDoubleOrNull()
        
        if (name.isBlank()) {
            _errorMessage.value = "Semester name cannot be blank."
            return
        }
        if (amt == null || amt <= 0) {
            _errorMessage.value = "Please enter a valid amount greater than 0."
            return
        }

        _isPublishing.value = true
        viewModelScope.launch {
            try {
                val setting = FeeSetting(
                    semesterName = name,
                    amount = amt,
                    dueDate = dueDate.value
                )
                repository.saveFeeSetting(setting)
                repository.publishFeeToAllHostelers(setting)
                semesterName.value = ""
                amount.value = ""
                dueDate.value = 0L
                _publishSuccess.value = true
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isPublishing.value = false
            }
        }
    }

    fun markAsPaid(recordId: String) {
        viewModelScope.launch {
            repository.markFeeAsPaid(recordId)
        }
    }

    fun resetPublishFlag() { _publishSuccess.value = false }
    
    fun clearError() { _errorMessage.value = null }
}
