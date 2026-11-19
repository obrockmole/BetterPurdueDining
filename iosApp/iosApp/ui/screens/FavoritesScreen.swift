import SwiftUI

struct FavoritesScreen: View {
    var body: some View {
        VStack {
            Text("Favorites")
                .font(.title2)
                .fontWeight(.bold)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .navigationTitle("Favorites")
        .enableSwipeBack()
    }
}
