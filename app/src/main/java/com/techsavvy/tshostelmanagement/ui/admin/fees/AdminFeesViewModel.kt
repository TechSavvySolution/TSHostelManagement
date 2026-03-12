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

    // Bulk delete state
    private val _selectedRecordIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedRecordIds: StateFlow<Set<String>> = _selectedRecordIds.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    // Form fields
    val semesterName = MutableStateFlow("")
    val amount = MutableStateFlow("")
    val upiId = MutableStateFlow("")
    val startDate = MutableStateFlow(0L)
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
                    upiId = upiId.value.trim(),
                    startDate = startDate.value,
                    dueDate = dueDate.value
                )
                repository.saveFeeSetting(setting)
                repository.publishFeeToAllHostelers(setting)
                semesterName.value = ""
                amount.value = ""
                upiId.value = ""
                startDate.value = 0L
                dueDate.value = 0L
                _publishSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Failed to publish: ${e.message}"
            } finally {
                _isPublishing.value = false
            }
        }
    }

    fun markAsPaid(recordId: String) {
        viewModelScope.launch { repository.markFeeAsPaid(recordId) }
    }

    // -- Selection Management --

    fun toggleSelection(recordId: String) {
        val current = _selectedRecordIds.value.toMutableSet()
        if (current.contains(recordId)) current.remove(recordId) else current.add(recordId)
        _selectedRecordIds.value = current
    }

    fun selectAll() {
        _selectedRecordIds.value = _feeRecords.value.map { it.id }.toSet()
    }

    fun selectAllForSemester(semesterName: String) {
        val ids = _feeRecords.value.filter { it.semesterName == semesterName }.map { it.id }.toSet()
        _selectedRecordIds.value = ids
    }

    fun clearSelection() {
        _selectedRecordIds.value = emptySet()
    }

    fun isSelected(recordId: String) = _selectedRecordIds.value.contains(recordId)

    // -- Bulk Delete --

    fun deleteSelected() {
        val ids = _selectedRecordIds.value.toList()
        if (ids.isEmpty()) return
        _isDeleting.value = true
        viewModelScope.launch {
            try {
                repository.deleteSelectedFeeRecords(ids)
                _selectedRecordIds.value = emptySet()
            } catch (e: Exception) {
                _errorMessage.value = "Delete failed: ${e.message}"
            } finally {
                _isDeleting.value = false
            }
        }
    }

    fun deleteAllForSemester(semesterName: String) {
        _isDeleting.value = true
        viewModelScope.launch {
            try {
                repository.deleteAllFeeRecordsForSemester(semesterName)
                _selectedRecordIds.value = emptySet()
            } catch (e: Exception) {
                _errorMessage.value = "Delete failed: ${e.message}"
            } finally {
                _isDeleting.value = false
            }
        }
    }

    fun resetPublishFlag() { _publishSuccess.value = false }
    fun clearError() { _errorMessage.value = null }
}
