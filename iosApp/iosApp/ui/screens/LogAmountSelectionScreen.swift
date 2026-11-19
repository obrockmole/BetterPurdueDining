import SwiftUI

struct LogAmountSelectionScreen: View {
	var body: some View {
		VStack {
			Text("Log Amount Selection")
				.font(.title2)
				.fontWeight(.bold)
		}
		.frame(maxWidth: .infinity, maxHeight: .infinity)
		.navigationTitle("Log Amount Selection")
		.enableSwipeBack()
	}
}
