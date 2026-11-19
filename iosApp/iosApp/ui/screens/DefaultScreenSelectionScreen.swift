import SwiftUI

struct DefaultScreenSelectionScreen: View {
    var body: some View {
        VStack {
            Text("Default Screen Selection")
                .font(.title2)
                .fontWeight(.bold)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .navigationTitle("Default Screen Selection")
        .enableSwipeBack()
    }
}
