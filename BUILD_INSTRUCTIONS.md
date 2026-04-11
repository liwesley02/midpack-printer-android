# Midpack 2 Android Build Instructions

## Prerequisites

1. **Android Studio** (latest stable version)
2. **Brother Print SDK** for Android
3. **Java Development Kit (JDK)** 17 or higher
4. **Android SDK** with minimum API level 22

## Step 1: Download Brother Print SDK

1. Visit: https://support.brother.com/g/s/es/dev/en/mobilesdk/android/index.html
2. Download the **Brother Print SDK v4.x** for Android
3. Extract the SDK and locate `BrotherPrintLibrary.aar`
4. Copy `BrotherPrintLibrary.aar` to `/home/wesley/clawd/midpack2-android-wrapper/android/app/libs/`

## Step 2: Pair Brother Printer via Bluetooth

Before building the app, pair your Brother QL-820NWB printer:

1. Enable Bluetooth on your Android device
2. Put the Brother printer in pairing mode
3. Go to Android Settings → Bluetooth
4. Pair with the Brother printer (it should appear as "Brother QL-820NWB" or similar)

## Step 3: Build the Android App

### Option A: Using Android Studio (Recommended)

1. Open Android Studio
2. Click "Open an existing Android Studio project"
3. Navigate to `/home/wesley/clawd/midpack2-android-wrapper/android/`
4. Wait for Gradle sync to complete
5. Connect your Android device via USB or use an emulator
6. Click the green "Run" button (or press Shift+F10)
7. Select your device from the deployment target list

### Option B: Using Command Line

```bash
cd /home/wesley/clawd/midpack2-android-wrapper/android

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Or build and install in one command
./gradlew installDebug
```

The APK will be generated at:
`android/app/build/outputs/apk/debug/app-debug.apk`

## Step 4: Test the Print Integration

1. Launch the Midpack 2 app on your Android device
2. Navigate to the Labels page
3. Click "Connect Bluetooth" button
4. The app should find your paired Brother printer
5. You should see a toast message: "✓ Connected to Brother QL-820NWB"
6. Try printing a test label by tapping "Print" on an order
7. The printer should physically print the label
8. The app will only mark the order as "printed" after the physical print completes

## Step 5: Build Release APK (For Production)

```bash
cd /home/wesley/clawd/midpack2-android-wrapper/android

# Generate release APK (requires signing key)
./gradlew assembleRelease
```

**Note:** Release builds require a signing key. You'll need to:
1. Generate a keystore file
2. Configure signing in `android/app/build.gradle`
3. Set `KEYSTORE_PASSWORD` and `KEY_PASSWORD` environment variables

## Troubleshooting

### "Brother SDK not found"
- Verify `BrotherPrintLibrary.aar` exists in `android/app/libs/`
- Check that `build.gradle` includes: `implementation files('libs/BrotherPrintLibrary.aar')`

### "Bluetooth permission denied"
- Check `AndroidManifest.xml` has required Bluetooth permissions
- Grant Bluetooth permissions when prompted on Android 12+

### "No Brother printer found"
- Ensure printer is paired in Android Bluetooth settings first
- Check printer is powered on and in range
- Try forgetting and re-pairing the device

### "Print timeout"
- Check printer has paper loaded
- Verify Bluetooth connection is stable
- Check printer is not in error state (cover open, paper jam, etc.)

### Build fails with "SDK not found"
- Set `ANDROID_HOME` environment variable to your Android SDK path
- Example: `export ANDROID_HOME=/home/wesley/Android/Sdk`

## Architecture Notes

### Async Print Flow

The Android bridge now uses **callback-based async communication**:

1. TypeScript calls `window.AndroidPrintBridge.printBrotherLabelJson(json, callbackId)`
2. Android executes print job on background thread
3. Android waits for Brother SDK to complete the physical print
4. Android calls JavaScript callback: `window.printCallbacks[callbackId](success, message)`
5. TypeScript resolves Promise and marks order as printed

This ensures orders are only marked "printed" after the physical print succeeds.

### Files Modified

- `android/app/src/main/java/com/happykitchens/midpack2/MainActivity.java` - Implements Brother SDK integration
- `android/app/build.gradle` - Added Brother SDK dependency
- `dash-timer-buddy/src/lib/androidPrintBridge.ts` - Callback-based async bridge
- `dash-timer-buddy/src/pages/MidpackLabelsPage.tsx` - Auto-advance print queue

## Next Steps

1. Test thoroughly with real printer hardware
2. Implement error recovery for print failures
3. Add printer status checking before print
4. Consider adding print preview functionality
5. Add settings UI for printer selection
