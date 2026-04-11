# Building Midpack 2 Android App on a Tablet

This guide shows how to build the Android app **directly on a tablet** without needing a desktop computer.

## Option 1: Using Termux (Recommended for Tablets)

Termux is a full Linux environment that runs on Android tablets.

### Step 1: Install Termux

1. Download **Termux** from F-Droid (NOT Google Play Store - the Play Store version is outdated)
   - Go to: https://f-droid.org/en/packages/com.termux/
   - Or install F-Droid app first, then install Termux from there

2. Open Termux and update packages:
```bash
pkg update && pkg upgrade
```

### Step 2: Install Build Tools

```bash
# Install required packages
pkg install git nodejs openjdk-17 gradle

# Install Android SDK command-line tools
pkg install android-tools
```

### Step 3: Clone the Project

```bash
# Clone your repository (or copy the project folder to tablet)
cd ~
git clone <your-repo-url>
cd midpack2-android-wrapper
```

### Step 4: Download Brother SDK

```bash
# Create libs directory
mkdir -p android/app/libs

# Download Brother SDK (you'll need to get the direct download URL)
# Visit: https://support.brother.com/g/s/es/dev/en/mobilesdk/android/index.html
# Right-click the download link and copy the URL
wget -O brother-sdk.zip "https://download.brother.com/welcome/..."

# Extract the AAR file
unzip brother-sdk.zip
find . -name "BrotherPrintLibrary.aar" -exec cp {} android/app/libs/ \;
```

### Step 5: Build the APK

```bash
cd android
chmod +x gradlew
./gradlew assembleDebug
```

The APK will be at: `android/app/build/outputs/apk/debug/app-debug.apk`

### Step 6: Install the APK

```bash
# Install directly on the tablet
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## Option 2: Using GitHub Actions (Cloud Build)

Build the APK in the cloud and download it to your tablet.

### Step 1: Create Workflow File

Create `.github/workflows/build-android.yml` in your repository:

```yaml
name: Build Android APK

on:
  push:
    branches: [ main ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        distribution: 'temurin'
        java-version: '17'

    - name: Download Brother SDK
      run: |
        mkdir -p android/app/libs
        # TODO: Add Brother SDK download URL
        # wget -O brother-sdk.zip "URL_HERE"
        # unzip brother-sdk.zip
        # cp BrotherPrintLibrary.aar android/app/libs/

    - name: Build with Gradle
      run: |
        cd android
        chmod +x gradlew
        ./gradlew assembleDebug

    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: android/app/build/outputs/apk/debug/app-debug.apk
```

### Step 2: Download Built APK

1. Push code to GitHub
2. Go to Actions tab in your repository
3. Click on the latest workflow run
4. Download the `app-debug` artifact
5. Transfer APK to tablet and install

---

## Option 3: Using Online IDE (Easiest)

Use a cloud-based Android development environment.

### Gitpod (Free Tier Available)

1. Go to: https://gitpod.io
2. Sign in with GitHub
3. Open your repository in Gitpod
4. Install Android SDK and build tools
5. Build the APK
6. Download to your tablet

---

## Simplified Approach: Pre-built APK

**The easiest solution for tablets:**

1. Set up a GitHub Actions workflow (Option 2 above)
2. Every time you push code, GitHub automatically builds the APK
3. Download the latest APK from GitHub Actions
4. Install on your tablet

This way you never need to build locally - just download and install.

---

## After Installation

### Pair the Printer

1. Go to tablet Settings → Bluetooth
2. Turn on Bluetooth
3. Put Brother QL-820NWB in pairing mode
4. Pair the device

### Test the App

1. Open Midpack 2 app
2. Navigate to Labels page
3. Tap "Connect Bluetooth"
4. Should see: "✓ Connected to Brother QL-820NWB"
5. Tap "Print" on an order to test

---

## Troubleshooting

### "Brother SDK not found"
The Brother SDK AAR file is required. You need to:
1. Download it from Brother's website
2. Extract `BrotherPrintLibrary.aar`
3. Place it in `android/app/libs/`

### Can't build on tablet - limited resources
Use GitHub Actions (cloud build) instead. No local resources needed.

### APK won't install
- Enable "Install from Unknown Sources" in Android settings
- Check APK is not corrupted during transfer
- Try uninstalling old version first
