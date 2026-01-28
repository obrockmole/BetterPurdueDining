package com.obrockmole.betterdining.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.obrockmole.betterdining.GetLocationMenuQuery
import com.obrockmole.betterdining.repository.MenuRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface MenuUiState {
    data class Success(val data: GetLocationMenuQuery.DiningCourtByName) : MenuUiState
    data object Error : MenuUiState
    data object Loading : MenuUiState
}

class MenuViewModel(private val menuRepository: MenuRepository) : ViewModel() {
    var menuUiState: MenuUiState by mutableStateOf(MenuUiState.Loading)
        private set

    fun getMenu(diningCourtName: String, date: String? = null) {
        viewModelScope.launch {
            menuUiState = MenuUiState.Loading
            try {
                val menuDate = date ?: LocalDate.now().toString()
                val result = menuRepository.getDiningCourtMenu(diningCourtName, menuDate)

                menuUiState = if (result != null) {
                    MenuUiState.Success(result)
                } else {
                    MenuUiState.Error
                }

            } catch (e: Exception) {
                menuUiState = MenuUiState.Error
            }
        }
    }
}

class MenuViewModelFactory(
    private val menuRepository: MenuRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(menuRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}