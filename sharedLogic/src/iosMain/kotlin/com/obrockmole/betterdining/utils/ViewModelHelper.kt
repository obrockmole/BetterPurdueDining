package com.obrockmole.betterdining.utils

import com.obrockmole.betterdining.database.BetterDiningDatabase
import com.obrockmole.betterdining.database.DataStoreFactory
import com.obrockmole.betterdining.database.DriverFactory
import com.obrockmole.betterdining.repository.*
import com.obrockmole.betterdining.viewmodel.*

object Dependencies {
    val database = BetterDiningDatabase(DriverFactory().createDriver())
    val datastore = DataStoreFactory().createDataStore()
}

fun getFavoritesViewModel(): FavoritesViewModel {
    return FavoritesViewModel(
        favoritesRepository = FavoritesRepository(
            favoriteItemQueries = Dependencies.database.favoriteItemQueries
        )
    )
}

fun getHomeViewModel(): HomeViewModel {
    return HomeViewModel(
        startLocationsRepository = StartLocationsRepository(),
        renamedCourtsRepository = RenamedCourtsRepository(
            renamedCourtQueries = Dependencies.database.renamedDiningCourtQueries
        )
    )
}

fun getItemViewModel(): ItemViewModel {
    return ItemViewModel(
        menuRepository = MenuRepository(),
        favoritesRepository = FavoritesRepository(
            favoriteItemQueries = Dependencies.database.favoriteItemQueries
        ),
        renamedItemsRepository = RenamedItemsRepository(
            renamedItemQueries = Dependencies.database.renamedItemQueries
        )
    )
}

fun getMenuViewModel(): MenuViewModel {
    return MenuViewModel(
        menuRepository = MenuRepository(),
        renamedItemsRepository = RenamedItemsRepository(
            renamedItemQueries = Dependencies.database.renamedItemQueries
        ),
        renamedCourtsRepository = RenamedCourtsRepository(
            renamedCourtQueries = Dependencies.database.renamedDiningCourtQueries
        )
    )
}

fun getSearchViewModel(): SearchViewModel {
    return SearchViewModel(
        searchRepository = SearchRepository()
    )
}

fun getSettingsViewModel(): SettingsViewModel {
    return SettingsViewModel(
        settingsRepository = SettingsRepository(),
        userPreferencesRepository = UserPreferencesRepository(
            dataStore = Dependencies.datastore
        )
    )
}

//fun getUpcomingFavoritesViewModel(): UpcomingFavoritesViewModel {
//    return UpcomingFavoritesViewModel(
//        repository = UpcomingFavoritesRepository(
//            favorites = Dependencies.database.favoriteItemQueries
//        )
//    )
//}