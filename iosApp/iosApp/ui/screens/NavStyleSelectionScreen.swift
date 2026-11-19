import SwiftUI

struct NavStyleSelectionScreen: View {
	var body: some View {
		VStack {
			Text("Nav Style Selection")
				.font(.title2)
				.fontWeight(.bold)
		}
		.frame(maxWidth: .infinity, maxHeight: .infinity)
		.navigationTitle("Favorites")
		.enableSwipeBack()
	}
}
