import SwiftUI

struct ThemeSelectionScreen: View {
	var body: some View {
		VStack {
			Text("Theme Selection")
				.font(.title2)
				.fontWeight(.bold)
		}
		.frame(maxWidth: .infinity, maxHeight: .infinity)
		.navigationTitle("Theme Selection")
		.enableSwipeBack()
	}
}
