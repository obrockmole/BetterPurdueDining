# Better Purdue Dining

Tired of Purdue's dining court menu app randomly crashing, their lack of features, or questionable design choices? If so then the Better Purdue Dining app is perfect. Built from the ground up, it was designed to be both familiar, taking cues from the original, but also different. Expanding on the original feature set, the Better Purdue Dining app is all one needs to peruse daily and weekly menu options how you want to do it.

## Features

- **API Integration**: Menu items are fetched directly from Purdue ensuring accurate menus for each meal.
- **Search**: Find the specific food you want by searching for it.
- **Nutrition**: View nutritional information for all items.
- **Renaming**: Give dining courts and menu items custom names to make it more personal.
- **Favorites**: Favorite items and see when they are coming up to know where to eat.
- **Themes**: Any app in 2026 should have at least a light and dark theme. Those are included in addition to a Material and _rainbow_ theme.
- **On Device Storage**: Everything is kept on device such as favorites and custom names. Nothing is ever sent anywhere for any reason and never will be.
- **Universal UI**: Get the same amazing experience on Android, iOS, and Windows/Mac desktop devices. Same UI, same features, same everything.

## Installation

There are two ways to install the Better Purdue Dining app:

### Download a Release

1. Download the latest release files from [releases](https://github.com/obrockmole/BetterPurdueDining/releases).
2. Run the desired release file for your platform on your device.

### Build from source
#### Prerequisites

* Android Studio (only for building Android)
* Xcode (only for building iOS)
* Java 17

#### Building

1. Clone the repo
    ```sh
    git clone [https://github.com/obrockmole/BetterPurdueDining.git](https://github.com/obrockmole/BetterPurdueDining.git)
    ```
2. To build the Android app, open the project in Android Studio and navigate to Build > Generate App Bundles or APKs > Generate APKs.
3. To build the Desktop app, run the Gradle packaging commands in your terminal depending on operating system.
  For Linux:
  ```sh
  ./gradlew :desktopApp:packageReleaseDeb
  ```
  For Windows:
  ```sh
  ./gradlew :desktopApp:packageReleaseExe
  ./gradlew :desktopApp:packageReleaseMsi
  ```
  For MacOS:
  ```sh
  ./gradlew :desktopApp:packageReleaseDmg
  ```
4. To build the iOS app, open the `iosApp/iosApp.xcodeproj` file in Xcode and build the project for your device.