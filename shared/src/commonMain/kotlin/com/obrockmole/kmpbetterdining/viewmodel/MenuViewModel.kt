package com.obrockmole.kmpbetterdining.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.obrockmole.kmpbetterdining.models.DiningCourtIdMap
import com.obrockmole.kmpbetterdining.repository.MenuRepository
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

sealed interface MenuUiState {
    data class Success(val data: DiningCourtMenuDisplay?) : MenuUiState
    data class Error(val message: String) : MenuUiState
    data object Loading : MenuUiState
}

class MenuViewModel(
    private val menuRepository: MenuRepository
) : ViewModel() {
    var menuUiState: MenuUiState by mutableStateOf(MenuUiState.Loading)
        private set

    fun getMenu(name: String?, courtId: String?, date: String) {
        viewModelScope.launch {
            menuUiState = MenuUiState.Loading
            try {
                val id = courtId ?: (DiningCourtIdMap.diningCourtIdMap[name] ?: "")

                val result = menuRepository.getDiningCourtMenu(id, date)

                val mappedResult = result.let { diningCourt ->
                    DiningCourtMenuDisplay(
                        name = diningCourt.name,
                        courtId = diningCourt.id,
                        meals = diningCourt.dailyMenu?.meals?.map { meal ->
                            MealDisplay(
                                name = meal.name,
                                stations = meal.stations.map { station ->
                                    StationDisplay(
                                        name = station.name,
                                        items = station.items.map { item ->
                                            MenuItemDisplay(
                                                originalItem = item,
                                                displayName = item.specialName ?: item.item.name
                                            )
                                        }
                                    )
                                },
                                startTime = meal.startTime,
                                endTime = meal.endTime
                            )
                        } ?: emptyList()
                    )
                }

                menuUiState = MenuUiState.Success(mappedResult)

            } catch (e: Exception) {
                menuUiState = MenuUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}

class MenuViewModelFactory(
    private val menuRepository: MenuRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        if (modelClass == MenuViewModel::class) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(
                menuRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}