import SwiftUI

struct FoodLocationDetailScreen: View {
    let locationId: String
    let locationName: String

    var body: some View {
		VStack(spacing: 20) {
            Text(locationName)
                .font(.title2)

            Text(locationId)
                .font(.headline)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .navigationTitle(locationName)
        .enableSwipeBack()
    }
}
