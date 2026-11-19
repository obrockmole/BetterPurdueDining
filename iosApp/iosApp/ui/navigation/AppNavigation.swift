import SwiftUI
import UIKit
import sharedLogic

enum AppTab: String, CaseIterable, Identifiable {
    case home = "Home"
    case favorites = "Favorites"
    case settings = "Settings"

    var id: String { rawValue }
}

enum Route: Hashable {
    case location(id: String, name: String)
    case item
    case search
    case defaultScreen
    case theme
    case navStyle
    case logAmount
    case licenses
}

@MainActor
class NavigationController: ObservableObject {
    @Published var selectedTab: AppTab = .home
    @Published var path = NavigationPath()

    func navigate(to route: Route) {
        path.append(route)
    }

    func pop() {
        if !path.isEmpty {
            path.removeLast()
        }
    }

    func popToRoot() {
        path = NavigationPath()
    }
}


extension UINavigationController: @retroactive UIGestureRecognizerDelegate {
    override open func viewDidLoad() {
        super.viewDidLoad()
        interactivePopGestureRecognizer?.delegate = self
    }

    public func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
        return viewControllers.count > 1
    }

    public func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer) -> Bool {
        return viewControllers.count > 1
    }
}

struct EnableSwipeBackModifier: ViewModifier {
    func body(content: Content) -> some View {
        content.background(SwipeBackRepresentable())
    }
}

private struct SwipeBackRepresentable: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> SwipeBackController {
        SwipeBackController()
    }

    func updateUIViewController(_ uiViewController: SwipeBackController, context: Context) {}

    class SwipeBackController: UIViewController {
        override func viewDidAppear(_ animated: Bool) {
            super.viewDidAppear(animated)
            navigationController?.interactivePopGestureRecognizer?.isEnabled = true
        }
    }
}

extension View {
    func enableSwipeBack() -> some View {
        self.modifier(EnableSwipeBackModifier())
    }
}
