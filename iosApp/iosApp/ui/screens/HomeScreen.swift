import SwiftUI
import sharedLogic

private let LOG_TAG = "HomeScreen"

let diningCourtOptions = ["Earhart", "Ford", "Hillenbrand", "Wiley", "Windsor"]
let quickBiteOptionsFormal = [
    "1bowl at Meredith Hall", "Pete's Za at Tarkington Hall", "Sushi Boss at South Hall"
]

struct HomeScreen: View {
    let viewModel = ViewModelHelperKt.getHomeViewModel()
    @State private var uiState: HomeUiState = HomeUiStateLoading()

    var body: some View {
        NavigationStack {
            contentView
                .navigationTitle("Better Purdue Dining")
                .toolbar {
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button(action: {
							Logger.shared.LogInfo(tag: LOG_TAG, message: "Search button clicked.")
//							onNavigateToSearch()
                        }) {
                            Image(systemName: "magnifyingglass")
                        }
                    }
                }
                .task {
                    for await state in viewModel.homeUiState {
                        self.uiState = state
                        if state is HomeUiStateLoading {
                            Logger.shared.LogDebug(tag: LOG_TAG, message: "UI loading")
                        } else if let errorState = state as? HomeUiStateError {
                            Logger.shared.LogError(tag: LOG_TAG, message: "Failed to load UI: \(errorState.message)")
                        } else if state is HomeUiStateSuccess {
                            Logger.shared.LogDebug(tag: LOG_TAG, message: "UI loaded successfully")
                        }
                    }
                }
                .task {
                    for await pair in viewModel.selectedDiningCourt {
                        Logger.shared.LogDebug(tag: LOG_TAG, message: "selectedDiningCourtFromFav: \(pair.first ?? "nil")")
                        if let name = pair.first as? String, pair.second != nil {
                            Logger.shared.LogInfo(tag: LOG_TAG, message: "Navigating to dining court from favorites: \(name)")
//							onNavigateToFoodLocation(first, second)
                            viewModel.clearNavigation()
                        }
                    }
                }
                .onAppear {
                    loadLocations()
                }
                .refreshable {
                    loadLocations()
                }
        }
    }

    private func loadLocations() {
        let date = DateTime_.shared.getDate(timezone: DateTime_.shared.purdueTimeZone).description
        Logger.shared.LogDebug(tag: LOG_TAG, message: "Getting locations for date: \(date)")
        viewModel.getLocations(date: date)
    }

    @ViewBuilder
    private var contentView: some View {
        if uiState is HomeUiStateLoading {
            VStack {
                Spacer()
                ProgressView()
                    .scaleEffect(1.5)
                Spacer()
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else if let errorState = uiState as? HomeUiStateError {
            VStack(spacing: 12) {
                Spacer()
                Text("Error loading locations.")
                    .font(.headline)
                    .foregroundColor(.red)
                Text(errorState.message)
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)
                Button("Try Again") {
                    loadLocations()
                }
                .buttonStyle(.bordered)
                Spacer()
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else if let successState = uiState as? HomeUiStateSuccess, let data = successState.data {
            let (diningCourts, quickBites) = parseCategories(from: data)

            ScrollView {
                LazyVStack(alignment: .leading, spacing: 0) {
                    Text("Dining Courts")
                        .font(.title2)
                        .fontWeight(.bold)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 16)

                    ForEach(diningCourtOptions, id: \.self) { diningCourtName in
                        if let diningCourt = diningCourts.first(where: { $0.diningCourt.name == diningCourtName }) {
                            DiningCourtListItem(diningCourt: diningCourt) {
                                Logger.shared.LogInfo(tag: LOG_TAG, message: "Navigating to dining court: \(diningCourt.diningCourt.name)")
//								onNavigateToFoodLocation(name, id)
                            }
                            Divider()
                        }
                    }

                    Text("Quick Bites")
                        .font(.title2)
                        .fontWeight(.bold)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 16)

                    ForEach(quickBiteOptionsFormal, id: \.self) { quickBiteName in
                        if let quickBite = quickBites.first(where: { $0.diningCourt.name == quickBiteName }) {
                            QuickBiteListItem(quickBite: quickBite) {
                                let name: String
                                switch quickBite.diningCourt.name {
									case "1bowl at Meredith Hall": name = "1bowl"
									case "Pete's Za at Tarkington Hall": name = "Pete's Za"
									case "Sushi Boss at Meredith Hall", "Sushi Boss at South Hall": name = "Sushi Boss"
									default: name = quickBite.diningCourt.name
                                }
								
                                Logger.shared.LogInfo(tag: LOG_TAG, message: "Navigating to quick bite: \(name)")
//								onNavigateToFoodLocation(name, id)
                            }
                            Divider()
                        }
                    }
                }
            }
        } else {
            EmptyView()
        }
    }

    private func parseCategories(from data: [Any]) -> ([DiningCourtWithCustomName], [DiningCourtWithCustomName]) {
        var diningCourts: [DiningCourtWithCustomName] = []
        var quickBites: [DiningCourtWithCustomName] = []

        for item in data {
            if let pair = item as? KotlinPair<AnyObject, AnyObject>, let category = pair.first as? String, let courts = pair.second as? [DiningCourtWithCustomName] {
				if category == "Dining Courts" {
					diningCourts = courts
				} else if category == "Quick Bites" {
					quickBites = courts
				}
            }
        }
		
        return (diningCourts, quickBites)
    }
}

struct DiningCourtListItem: View {
    let diningCourt: DiningCourtWithCustomName
    let onClicked: () -> Void

    private var icon: String {
        switch diningCourt.diningCourt.name.lowercased() {
			case "earhart": return "earhart_icon"
			case "ford": return "ford_icon"
			case "hillenbrand": return "hillenbrand_icon"
			case "wiley": return "wiley_icon"
			case "windsor": return "windsor_icon"
			default: return "app_icon"
        }
    }

    private var mealInfo: (status: String, statusCaption: String?) {
        calculateMealInfo(menu: diningCourt.diningCourt.dailyMenu, court: diningCourt.diningCourt.name)
    }

    var body: some View {
        Button(action: onClicked) {
            HStack(spacing: 16) {
                Image(icon)
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 80, height: 80)

                VStack(alignment: .leading, spacing: 4) {
                    Text(diningCourt.customName ?? diningCourt.diningCourt.name)
                        .font(.headline)
                        .foregroundColor(.primary)

                    Text(mealInfo.status)
                        .font(.subheadline)
                        .foregroundColor(.primary)

                    if let subtitle = mealInfo.statusCaption {
                        Text(subtitle)
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }

                Spacer()

                Image(systemName: "chevron.right")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(Color(.tertiaryLabel))
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

struct QuickBiteListItem: View {
    let quickBite: DiningCourtWithCustomName
    let onClicked: () -> Void

    private var name: String {
        switch quickBite.diningCourt.name {
			case "1bowl at Meredith Hall": return "1bowl"
			case "Pete's Za at Tarkington Hall": return "Pete's Za"
			case "Sushi Boss at South Hall", "Sushi Boss at Meredith Hall": return "Sushi Boss"
			default: return quickBite.diningCourt.name
        }
    }

    private var icon: String {
        switch name.lowercased() {
			case "1bowl": return "onebowl_icon"
			case "pete's za": return "petes_icon"
			case "sushi boss": return "sushiboss_icon"
			default: return "app_icon"
        }
    }

    private var mealInfo: (status: String, statusCaption: String?) {
        calculateMealInfo(menu: quickBite.diningCourt.dailyMenu, court: quickBite.diningCourt.name)
    }

    var body: some View {
        Button(action: onClicked) {
            HStack(spacing: 16) {
                Image(icon)
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 80, height: 80)

                VStack(alignment: .leading, spacing: 4) {
                    Text(quickBite.customName ?? name)
                        .font(.headline)
                        .foregroundColor(.primary)

                    Text(mealInfo.status)
                        .font(.subheadline)
                        .foregroundColor(.primary)

                    if let subtitle = mealInfo.statusCaption {
                        Text(subtitle)
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }

                Spacer()

                Image(systemName: "chevron.right")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundColor(Color(.tertiaryLabel))
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

private func calculateMealInfo(menu dailyMenu: GetStartLocationsQuery.DailyMenu?, court: String) -> (status: String, statusCaption: String?) {
    guard let dailyMenu = dailyMenu else {
        return ("Closed", nil)
    }

    var calendar = Calendar.current
    if let purdueTimeZone = TimeZone(identifier: "America/New_York") {
        calendar.timeZone = purdueTimeZone
    }
    let currentHour = calendar.component(.hour, from: Foundation.Date())

    var currentMealIndex = -1

    for (index, meal) in dailyMenu.meals.enumerated() {
		if meal.status == MealStatus.open, let startTime = meal.startTime, let endTime = meal.endTime {
			if let startHour = parseHour(startTime), let endHour = parseHour(endTime) {
				if currentHour >= startHour && currentHour < endHour {
					currentMealIndex = index
					Logger.shared.LogDebug(tag: LOG_TAG, message: "\(court) OPEN. Current meal \(meal.name) (\(index))")
					break
				}
			}
        }
    }

    if currentMealIndex == -1 {
        Logger.shared.LogDebug(tag: LOG_TAG, message: "\(court) CLOSED")
        return ("Closed", nil)
    }

    let meal = dailyMenu.meals[currentMealIndex]
    let status = meal.status == MealStatus.open ? "Open" : meal.status.name.capitalized

    var statusCaption: String? = nil
    if let endTimeStr = meal.endTime, let formattedTime = formatEndTime(endTimeStr) {
		statusCaption = "Serving \(meal.name) until \(formattedTime)"
    }

    return (status, statusCaption)
}

private func parseDateTime(_ dateString: String) -> Foundation.Date? {
    let isoFormatter = ISO8601DateFormatter()
    isoFormatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
    if let date = isoFormatter.date(from: dateString) {
        return date
    }
	
    isoFormatter.formatOptions = [.withInternetDateTime]
    if let date = isoFormatter.date(from: dateString) {
        return date
    }
	
    let formatter = DateFormatter()
	formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
	formatter.timeZone = TimeZone(identifier: "America/New_York")
    return formatter.date(from: dateString)
}

private func parseHour(_ dateString: String) -> Int? {
    guard let date = parseDateTime(dateString) else { return nil }
    var calendar = Calendar.current
    if let purdueTimeZone = TimeZone(identifier: "America/New_York") {
        calendar.timeZone = purdueTimeZone
    }
	
    return calendar.component(.hour, from: date)
}

private func formatEndTime(_ dateString: String) -> String? {
    guard let date = parseDateTime(dateString) else { return nil }
    let formatter = DateFormatter()
    formatter.dateFormat = "h:mm a"
    formatter.timeZone = TimeZone.current
	
    return formatter.string(from: date)
}
