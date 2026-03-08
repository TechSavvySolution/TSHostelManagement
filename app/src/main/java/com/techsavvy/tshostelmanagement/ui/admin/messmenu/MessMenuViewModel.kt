package com.techsavvy.tshostelmanagement.ui.admin.messmenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techsavvy.tshostelmanagement.data.models.DayMenu
import com.techsavvy.tshostelmanagement.data.models.MessMenu
import com.techsavvy.tshostelmanagement.data.repositories.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessMenuViewModel @Inject constructor(
    private val repository: FirestoreRepository
) : ViewModel() {

    private val _menu = MutableStateFlow(MessMenu())
    val menu: StateFlow<MessMenu> = _menu.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess = _saveSuccess.asStateFlow()

    init {
        loadMenu()
    }

    private fun loadMenu() {
        viewModelScope.launch {
            repository.getMessMenu().collect { _menu.value = it }
        }
    }

    fun updateMeal(day: String, meal: String, value: String) {
        val current = _menu.value
        val updatedDay = when (day) {
            "Monday"    -> current.monday.update(meal, value)
            "Tuesday"   -> current.tuesday.update(meal, value)
            "Wednesday" -> current.wednesday.update(meal, value)
            "Thursday"  -> current.thursday.update(meal, value)
            "Friday"    -> current.friday.update(meal, value)
            "Saturday"  -> current.saturday.update(meal, value)
            "Sunday"    -> current.sunday.update(meal, value)
            else -> return
        }
        _menu.value = when (day) {
            "Monday"    -> current.copy(monday = updatedDay)
            "Tuesday"   -> current.copy(tuesday = updatedDay)
            "Wednesday" -> current.copy(wednesday = updatedDay)
            "Thursday"  -> current.copy(thursday = updatedDay)
            "Friday"    -> current.copy(friday = updatedDay)
            "Saturday"  -> current.copy(saturday = updatedDay)
            "Sunday"    -> current.copy(sunday = updatedDay)
            else -> current
        }
    }

    fun saveMenu() {
        viewModelScope.launch {
            repository.saveMessMenu(_menu.value)
            _saveSuccess.value = true
        }
    }

    fun resetSaveFlag() { _saveSuccess.value = false }
}

private fun DayMenu.update(meal: String, value: String): DayMenu = when (meal) {
    "Breakfast" -> copy(breakfast = value)
    "Lunch"     -> copy(lunch = value)
    "Dinner"    -> copy(dinner = value)
    else -> this
}
