import SwiftUI

struct ItemDetailScreen: View {
    var body: some View {
		VStack {
			Text("Item Details Screen")
				.font(.title2)
				.fontWeight(.bold)
		}
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .navigationTitle("Item Details Screen")
        .enableSwipeBack()
    }
}
