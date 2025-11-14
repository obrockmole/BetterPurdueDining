package com.obrockmole.betterdining.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrockmole.betterdining.GetStartLocationsQuery
import com.obrockmole.betterdining.repository.StartLocationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface HomeUiState {
    data class Success(val data: List<GetStartLocationsQuery.DiningCourtCategory>?) : HomeUiState
    data object Error : HomeUiState
    data object Loading : HomeUiState
}

class HomeViewModel(private val startLocationsRepository: StartLocationsRepository) : ViewModel() {
    private val _selectedDiningCourt = MutableStateFlow<String?>(null)
    val selectedDiningCourt: StateFlow<String?> = _selectedDiningCourt

    private val _selectedMealName = MutableStateFlow<String?>(null)
    val selectedMealName: StateFlow<String?> = _selectedMealName

    private val _selectedDate = MutableStateFlow<String?>(null)
    val selectedDate: StateFlow<String?> = _selectedDate

    var homeUiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

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

    fun getLocations(date: String? = null) {
        viewModelScope.launch {
            homeUiState = HomeUiState.Loading
            try {
                val menuDate = date ?: LocalDate.now().toString()
                val result = startLocationsRepository.getStartLocations(menuDate)

                homeUiState = if (result != null) {
                    HomeUiState.Success(result)
                } else {
                    HomeUiState.Error
                }

            } catch (e: Exception) {
                homeUiState = HomeUiState.Error
            }
        }
    }
}

