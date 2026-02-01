package com.obrockmole.betterdining.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.obrockmole.betterdining.GetStartLocationsQuery
import com.obrockmole.betterdining.repository.RenamedCourtsRepository
import com.obrockmole.betterdining.repository.StartLocationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DiningCourtWithCustomName(
    val diningCourt: GetStartLocationsQuery.DiningCourt,
    val customName: String?
)

sealed interface HomeUiState {
    data class Success(val data: List<Pair<String, List<DiningCourtWithCustomName>>>?) : HomeUiState
    data object Error : HomeUiState
    data object Loading : HomeUiState
}

class HomeViewModel(
    private val startLocationsRepository: StartLocationsRepository,
    private val renamedCourtsRepository: RenamedCourtsRepository
    ) : ViewModel() {
    private val _selectedDiningCourt = MutableStateFlow<String?>(null)
    private val _selectedDiningCourtId = MutableStateFlow<String?>(null)
    val selectedDiningCourt: StateFlow<Pair<String?, String?>> = MutableStateFlow(
        Pair(_selectedDiningCourt.value, _selectedDiningCourtId.value)
    )

    private val _selectedMealName = MutableStateFlow<String?>(null)
    val selectedMealName: StateFlow<String?> = _selectedMealName

    private val _selectedDate = MutableStateFlow<String?>(null)
    val selectedDate: StateFlow<String?> = _selectedDate

    private val _selectedItem = MutableStateFlow<String?>(null)
    val selectedItem: StateFlow<String?> = _selectedItem

    var homeUiState: HomeUiState by mutableStateOf(HomeUiState.Loading)
        private set

    fun navigateToMenu(diningCourt: String, diningCourtId: String?, mealName: String, date: String, item: String) {
        _selectedDiningCourt.value = diningCourt
        _selectedDiningCourtId.value = diningCourtId
        _selectedMealName.value = mealName
        _selectedDate.value = date
        _selectedItem.value = item
    }

    fun clearNavigation() {
        _selectedDiningCourt.value = null
        _selectedDiningCourtId.value = null
        _selectedMealName.value = null
        _selectedDate.value = null
        _selectedItem.value = null
    }

    fun getLocations(date: String) {
        viewModelScope.launch {
            homeUiState = HomeUiState.Loading
            try {
                val startLocations = startLocationsRepository.getStartLocations(date)
                val result = startLocations?.map { category ->
                    val courtsWithCustomNames = category.diningCourts.map { court ->
                        val customName = renamedCourtsRepository.getRenamedCourt(court.id) ?.customName
                        DiningCourtWithCustomName(court, customName)
                    }
                    Pair(category.name, courtsWithCustomNames)
                }
                homeUiState = HomeUiState.Success(result)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error getting locations: ${e.message}")
                homeUiState = HomeUiState.Error
            }
        }
    }
}

class HomeViewModelFactory(
    private val startLocationsRepository: StartLocationsRepository,
    private val renamedCourtsRepository: RenamedCourtsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(startLocationsRepository, renamedCourtsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}