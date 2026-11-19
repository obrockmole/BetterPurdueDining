import SwiftUI

struct SearchScreen: View {
	var body: some View {
		VStack {
			Text("Search Screen")
				.font(.title2)
				.fontWeight(.bold)
		}
		.frame(maxWidth: .infinity, maxHeight: .infinity)
		.navigationTitle("Search Screen")
		.enableSwipeBack()
	}
}
