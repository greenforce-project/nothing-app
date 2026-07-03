# nothing

A challenge where the goal is to do absolutely nothing.

Start the timer, put your phone down, and avoid touching the
screen for as long as possible.

Sounds easy.

It is not.

## How to play

1. Open the app.
2. Tap **start doing nothing**.
3. Do not touch the screen.
4. Do not leave the app.
5. Try to beat your best time.

Touching the screen or leaving the application ends the
challenge.

## Features

- A real-time do-nothing timer
- Personal best time
- Attempt counter
- Random failure messages
- Local statistics
- Shareable results
- Automatic light and dark themes
- Completely offline
- No advertisements
- No analytics
- No account
- No unnecessary permissions

## Screenshots

### Light mode

```text
┌─────────────────────────────┐
│                             │
│           nothing           │
│                             │
│         00:42.391           │
│                             │
│     don't touch anything    │
│                             │
│   best 00:58   attempts 7   │
│                             │
└─────────────────────────────┘
```

### Dark mode

The same experience, except darker.

## Challenge milestones

The status message changes automatically as the player reaches
longer periods of inactivity:

- 10 seconds — Almost Nothing
- 30 seconds — Professionally Inactive
- 1 minute — Certified Nothing
- 5 minutes — Master of Absolutely Nothing
- 10 minutes — Are You Still There?

## Challenge rules

The current challenge ends when the user:

- touches the application screen;
- leaves the application;
- opens another application;
- opens a system surface that removes window focus;
- locks the device.

The screen remains awake while a challenge is active.

## Privacy

[Read the full privacy policy](docs/PRIVACY.md).

The application does not:

- connect to the internet;
- collect personal information;
- display advertisements;
- use analytics services;
- request sensitive permissions;
- run a background tracking service;
- upload statistics anywhere.

Best time and attempt count are stored locally on the device.

## Android support

- Minimum Android version: Android 5.0
- Minimum SDK: 21
- Target SDK: 36
- Java version: 17

## Technology

- Java
- Android XML layouts
- SharedPreferences
- Android Gradle Plugin
- No third-party application libraries

## Build requirements

- JDK 17
- Android SDK 36
- Gradle Wrapper

## Build

Clone the repository:

```bash
git clone https://github.com/fadlyasmuhammad-mbg/nothing.git
cd nothing
```

Build the debug APK:

```bash
./gradlew clean :app:assembleDebug --no-daemon
```

The debug APK will be generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Build the release APK:

```bash
./gradlew clean :app:assembleRelease --no-daemon
```

The release APK will be generated at:

```text
app/build/outputs/apk/release/app-release.apk
```

## Project structure

```text
nothing/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/fadlyas07/donothing/
│       │   └── MainActivity.java
│       └── res/
│           ├── drawable/
│           ├── layout/
│           ├── mipmap-anydpi/
│           ├── mipmap-anydpi-v26/
│           ├── values/
│           ├── values-night/
│           ├── values-v31/
│           └── values-night-v31/
├── gradle/
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
└── README.md
```

## Developer

Developed by **Muhammad Fadly A.S.**

GitHub: **@fadlyas07**

## Frequently asked questions

### What is the objective?

Do nothing for as long as possible.

### What happens when I touch the screen?

You did something. The challenge ends.

### Does the app work offline?

Yes.

### Does the app collect data?

No.

### Is doing nothing difficult?

Apparently.

---

Made with nothing.
