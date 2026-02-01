package com.obrockmole.betterdining.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.obrockmole.betterdining.database.RenamedDiningCourt
import com.obrockmole.betterdining.repository.MenuRepository
import com.obrockmole.betterdining.repository.RenamedCourtsRepository
import com.obrockmole.betterdining.repository.RenamedItemsRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

sealed interface MenuUiState {
    data class Success(val data: DiningCourtMenuDisplay?) : MenuUiState
    data object Error : MenuUiState
    data object Loading : MenuUiState
}

class MenuViewModel(
    private val menuRepository: MenuRepository,
    private val renamedItemsRepository: RenamedItemsRepository,
    private val renamedCourtsRepository: RenamedCourtsRepository
) : ViewModel() {
    var menuUiState: MenuUiState by mutableStateOf(MenuUiState.Loading)
        private set

    var isRenamed by mutableStateOf(false)
        private set

    var renamedName by mutableStateOf("")
        private set

    fun getMenu(diningCourtName: String, date: String? = null) {
        viewModelScope.launch {
            menuUiState = MenuUiState.Loading
            try {
                val menuDate = date ?: LocalDate.now().toString()
                val result = menuRepository.getDiningCourtMenu(diningCourtName, menuDate)

                val mappedResult = result?.let { diningCourtByName ->
                    DiningCourtMenuDisplay(
                        name = diningCourtByName.name,
                        courtId = diningCourtByName.id,
                        meals = diningCourtByName.dailyMenu?.meals.let {
                            it?.map { meal ->
                                MealDisplay(
                                    name = meal.name,
                                    stations = meal.stations.map { station ->
                                        StationDisplay(
                                            name = station.name,
                                            items = station.items.map { item ->
                                                MenuItemDisplay(
                                                    originalItem = item,
                                                    displayName = renamedItemsRepository.getRenamedItem(
                                                        item.item.itemId
                                                    )?.customName ?: item.specialName ?: item.item.name
                                                )
                                            }
                                        )
                                    },
                                    startTime = meal.startTime,
                                    endTime = meal.endTime
                                )
                            } ?: emptyList()
                        }
                    )
                }

                val renamedCourt = renamedCourtsRepository.getRenamedCourt(mappedResult?.courtId ?: "")
                if (renamedCourt != null) {
                    isRenamed = true
                    renamedName = renamedCourt.customName
                }

                menuUiState = if (result != null) {
                    MenuUiState.Success(mappedResult)
                } else {
                    MenuUiState.Error
                }

            } catch (e: Exception) {
                menuUiState = MenuUiState.Error
            }
        }
    }

    fun renameDiningCourt(courtId: String, customName: String) {
        viewModelScope.launch {
            val renamedCourt = RenamedDiningCourt(courtId, customName)
            renamedCourtsRepository.insert(renamedCourt)
            isRenamed = true
            renamedName = customName
        }
    }
}

class MenuViewModelFactory(
    private val menuRepository: MenuRepository,
    private val renamedItemsRepository: RenamedItemsRepository,
    private val renamedCourtsRepository: RenamedCourtsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(menuRepository, renamedItemsRepository, renamedCourtsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}