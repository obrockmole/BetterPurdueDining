package com.obrockmole.betterdining.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {
    private val _selectedDiningCourt = MutableStateFlow<String?>(null)
    val selectedDiningCourt: StateFlow<String?> = _selectedDiningCourt

    private val _selectedMealName = MutableStateFlow<String?>(null)
    val selectedMealName: StateFlow<String?> = _selectedMealName

    private val _selectedDate = MutableStateFlow<String?>(null)
    val selectedDate: StateFlow<String?> = _selectedDate

    fun navigateToMenu(diningCourt: String, mealName: String, date: String) {
        _selectedDiningCourt.value = diningCourt
        _selectedMealName.value = mealName
        _selectedDate.value = date
    }

    fun clearNavigation() {
        _selectedDiningCourt.value = null
        _selectedMealName.value = null
        _selectedDate.value = null
    }
}

