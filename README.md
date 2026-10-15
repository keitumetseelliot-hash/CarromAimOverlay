# CarromAimOverlay

An Android prototype that captures the screen with Android MediaProjection and draws a **visual-only** carrom aiming guide as a floating overlay.

## Features

- Android 8.0+.
- Screen capture via MediaProjection.
- Floating overlay via `SYSTEM_ALERT_WINDOW`.
- Dependency-light Kotlin implementation.
- Coarse board/target detection prototype.
- Draws striker → target → nearest-pocket guide.
- Does **not** inject taps, drag the striker, or automate gameplay.

## Build

Open the project in Android Studio with JDK 17 and let Gradle sync.

```bash
./gradlew assembleDebug
```

APK output:

`app/build/outputs/apk/debug/app-debug.apk`

## Install

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

Then open the app, grant overlay permission, press **Start Aim Overlay**, and approve Android's screen-capture prompt.

## Notes

The detector is intentionally a dependency-free prototype. Real boards vary by game, theme, scaling, and device. The next improvement is replacing `ScreenDetector.kt` with a robust computer-vision pipeline (for example OpenCV) that detects the board corners, pockets, striker, and individual coins and then computes collision/reflection geometry.

The overlay remains visual-only.
