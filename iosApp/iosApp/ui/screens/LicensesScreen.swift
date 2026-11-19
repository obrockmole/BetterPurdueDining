import SwiftUI

struct LicensesScreen: View {
	var body: some View {
		VStack {
			Text("Licenses")
				.font(.title2)
				.fontWeight(.bold)
		}
		.frame(maxWidth: .infinity, maxHeight: .infinity)
		.navigationTitle("Licenses")
		.enableSwipeBack()
	}
}
