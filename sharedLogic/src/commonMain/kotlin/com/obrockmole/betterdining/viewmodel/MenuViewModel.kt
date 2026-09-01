package com.obrockmole.betterdining.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.obrockmole.betterdining.database.RenamedDiningCourt
import com.obrockmole.betterdining.repository.MenuRepository
import com.obrockmole.betterdining.repository.RenamedCourtsRepository
import com.obrockmole.betterdining.repository.RenamedItemsRepository
import com.obrockmole.betterdining.utils.DiningCourtIdMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

sealed interface MenuUiState {
    data class Success(val data: DiningCourtMenuDisplay?) : MenuUiState
    data class Error(val message: String) : MenuUiState
    data object Loading : MenuUiState
}

class MenuViewModel(
    private val menuRepository: MenuRepository,
    private val renamedItemsRepository: RenamedItemsRepository,
    private val renamedCourtsRepository: RenamedCourtsRepository
) : ViewModel() {
    private val _menuUiState = MutableStateFlow<MenuUiState>(MenuUiState.Loading)
    val menuUiState: StateFlow<MenuUiState> = _menuUiState

    private val _isRenamed = MutableStateFlow(false)
    val isRenamed: StateFlow<Boolean> = _isRenamed

    private val _renamedName = MutableStateFlow("")
    val renamedName: StateFlow<String> = _renamedName

    fun getMenu(name: String?, courtId: String?, date: String) {
        viewModelScope.launch {
            _menuUiState.value = MenuUiState.Loading
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
                                            ItemDisplay(
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
                    )
                }

                val renamedCourt = renamedCourtsRepository.getRenamedCourt(mappedResult.courtId)
                if (renamedCourt != null) {
                    _isRenamed.value = true
                    _renamedName.value = renamedCourt.customName
                }

                _menuUiState.value = MenuUiState.Success(mappedResult)

            } catch (e: Exception) {
                _menuUiState.value = MenuUiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun renameDiningCourt(courtId: String, customName: String) {
        viewModelScope.launch {
            val renamedCourt = RenamedDiningCourt(courtId, customName)

            if (customName.isEmpty()) {
                renamedCourtsRepository.delete(renamedCourt)
                _isRenamed.value = false
            } else {
                renamedCourtsRepository.insert(renamedCourt)
                _isRenamed.value = true
                _renamedName.value = customName
            }
        }
    }
}

class MenuViewModelFactory(
    private val menuRepository: MenuRepository,
    private val renamedItemsRepository: RenamedItemsRepository,
    private val renamedCourtsRepository: RenamedCourtsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        if (modelClass == MenuViewModel::class) {
            @Suppress("UNCHECKED_CAST")
            return MenuViewModel(
                menuRepository,
                renamedItemsRepository,
                renamedCourtsRepository
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}