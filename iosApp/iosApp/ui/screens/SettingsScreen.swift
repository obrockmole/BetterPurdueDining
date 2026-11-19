import SwiftUI

struct SettingsScreen: View {
    @EnvironmentObject private var navController: NavigationController

    var body: some View {
        List {
            Section("Settings") {
                Button(action: {
					navController.navigate(to: .defaultScreen)
                }) {
                    HStack {
                        Text("Default Screen")
                        Spacer()
                        Image(systemName: "chevron.right")
                            .font(.system(size: 14))
                            .foregroundColor(.secondary)
                    }
                }
                .foregroundColor(.primary)

                Button(action: {
					navController.navigate(to: .theme)
                }) {
                    HStack {
                        Text("Theme")
                        Spacer()
                        Image(systemName: "chevron.right")
                            .font(.system(size: 14))
                            .foregroundColor(.secondary)
                    }
                }
                .foregroundColor(.primary)

                Button(action: {
					navController.navigate(to: .navStyle)
                }) {
                    HStack {
                        Text("Navigation Style")
                        Spacer()
                        Image(systemName: "chevron.right")
                            .font(.system(size: 14))
                            .foregroundColor(.secondary)
                    }
                }
                .foregroundColor(.primary)

                Button(action: {
					navController.navigate(to: .logAmount)
                }) {
                    HStack {
                        Text("Logging")
                        Spacer()
                        Image(systemName: "chevron.right")
                            .font(.system(size: 14))
                            .foregroundColor(.secondary)
                    }
                }
                .foregroundColor(.primary)

                Button(action: {
					navController.navigate(to: .licenses)
                }) {
                    HStack {
                        Text("Licenses")
                        Spacer()
                        Image(systemName: "chevron.right")
                            .font(.system(size: 14))
                            .foregroundColor(.secondary)
                    }
                }
                .foregroundColor(.primary)
            }
        }
        .navigationTitle("Settings")
        .enableSwipeBack()
    }
}
