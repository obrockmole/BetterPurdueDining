package com.obrockmole.betterdining.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obrockmole.betterdining.models.Meal
import com.obrockmole.betterdining.repository.MenuRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface MenuUiState {
    data class Success(val meals: List<Meal>) : MenuUiState
    data object Error : MenuUiState
    data object Loading : MenuUiState
}

class MenuViewModel(private val menuRepository: MenuRepository) : ViewModel() {

    var menuUiState: MenuUiState by mutableStateOf(MenuUiState.Loading)
        private set

    fun getMenu(diningCourtName: String) {
        viewModelScope.launch {
            menuUiState = MenuUiState.Loading
            val date = LocalDate.now().toString()
            val result = menuRepository.getDiningCourtMenu(diningCourtName, date)
            menuUiState = result.fold(
                onSuccess = {
                    if (it.data.diningCourtByName.dailyMenu.meals.isNotEmpty()) {
                        MenuUiState.Success(it.data.diningCourtByName.dailyMenu.meals)
                    } else {
                        MenuUiState.Error
                    }
                },
                onFailure = {
                    MenuUiState.Error
                }
            )
        }
    }
}

