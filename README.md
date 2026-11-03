# PejKeyboard

A lightweight native Android keyboard built with Kotlin and Android Views.

PejKeyboard is an experimental Android input method focused on a simple, clean typing experience with custom Persian and English layouts.

## Features

- Custom Persian keyboard layout
- Custom English keyboard layout
- Numeric and symbol input mode
- English Shift and Caps Lock
- Space and Persian ZWNJ (zero-width non-joiner) keys
- Backspace and Enter keys
- Light and dark theme support based on system settings
- Native Android UI — no Jetpack Compose

## Screenshots

Screenshots will be added as the keyboard UI evolves.

## Requirements

- Android 10 (API 29) or later
- JDK 17 or later
- Android SDK 36
- Gradle Wrapper (included in the repository)

## Build

Clone the repository:

```bash
git clone https://github.com/pejmands/PejKeyboard.git
cd PejKeyboard
```

Build the debug APK:

**Windows:**

```powershell
.\gradlew.bat assembleDebug
```

**Linux / macOS:**

```bash
./gradlew assembleDebug
```

The generated APK will be available at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Install

Install the APK using ADB:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

After installation:

1. Open Android Settings.
2. Navigate to the keyboard or input method settings.
3. Enable PejKeyboard.
4. Select PejKeyboard as the active input method.

The exact settings path may vary depending on the device manufacturer.

## Technology Stack

- Kotlin
- Android SDK
- Android `InputMethodService`
- Native Android Views
- Gradle 9.1.0
- Android Gradle Plugin 9.0.1

## Project Structure

```text
PejKeyboard/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           ├── kotlin/
│           │   └── com/pejmands/pejkeyboard/
│           │       ├── MainActivity.kt
│           │       └── PejKeyboardService.kt
│           ├── res/
│           └── AndroidManifest.xml
├── gradle/
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
└── README.md
```

## Development Status

PejKeyboard is under active development.

The current implementation provides the basic keyboard layouts and input functionality. Features such as word suggestions, predictive typing, customization settings, and advanced input handling are planned for future development.

## License

No license has been specified yet.
