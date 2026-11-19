import SwiftUI
import sharedLogic

struct ContentView: View {
    @StateObject private var navController = NavigationController()
    private let settingsViewModel = ViewModelHelperKt.getSettingsViewModel()

    var body: some View {
        NavigationStack(path: $navController.path) {
            TabView(selection: $navController.selectedTab) {
                HomeScreen()
                    .tabItem {
                        Label("Home", systemImage: "house")
                    }
                    .tag(AppTab.home)

                FavoritesScreen()
                    .tabItem {
                        Label("Favorites", systemImage: "heart")
                    }
                    .tag(AppTab.favorites)

                SettingsScreen()
                    .tabItem {
                        Label("Settings", systemImage: "gearshape")
                    }
                    .tag(AppTab.settings)
            }
            .navigationDestination(for: Route.self) { route in
                destinationView(for: route)
            }
        }
        .environmentObject(navController)
    }

    @ViewBuilder
    private func destinationView(for route: Route) -> some View {
        switch route {
			case .location(let id, let name):
				FoodLocationDetailScreen(
					locationId: id,
					locationName: name
				)
			case .item:
				ItemDetailScreen()
			case .search:
				SearchScreen()
			case .defaultScreen:
				DefaultScreenSelectionScreen()
			case .theme:
				ThemeSelectionScreen()
			case .navStyle:
				NavStyleSelectionScreen()
			case .logAmount:
				LogAmountSelectionScreen()
			case .licenses:
				LicensesScreen()
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
		ContentView()
    }
}
