import SwiftUI
import sharedLogic

struct ContentView: View {
	var body: some View {
		VStack(spacing: 20) {
			Image(systemName: "applelogo")
				.imageScale(.large)
				.font(.system(size: 40))
			
			Text("Hello World!")
				.font(.title)
				.fontWeight(.bold)
		}
		.padding()
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
