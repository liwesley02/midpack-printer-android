# Tablet Deployment Guide - Midpack 2 Print System

**Goal:** Install the Midpack 2 app on tablets for label printing at each kitchen location.

## Quick Start (Recommended Method)

### 1. Download Pre-Built APK

Once GitHub Actions is set up (see below), simply:

1. Go to: https://github.com/YOUR_USERNAME/YOUR_REPO/actions
2. Click on the latest "Build Android APK" workflow
3. Download the `midpack2-debug-apk` artifact
4. Extract the `.apk` file from the zip
5. Transfer to your tablet (email, USB, cloud storage, etc.)

### 2. Install on Tablet

1. On the tablet, enable "Install from Unknown Sources":
   - Settings → Security → Unknown Sources (toggle ON)
   - Or: Settings → Apps → Special Access → Install Unknown Apps → [Your Browser/File Manager] → Allow

2. Open the APK file on the tablet
3. Tap "Install"
4. Grant permissions when prompted

### 3. Pair the Printer

1. Power on your Brother QL-820NWB printer
2. Put it in pairing mode (refer to printer manual)
3. On tablet: Settings → Bluetooth → Turn ON
4. Find "Brother QL-820NWB" in available devices
5. Tap to pair

### 4. Test Printing

1. Open Midpack 2 app
2. Go to Labels page
3. Tap "Connect Bluetooth" button
4. Should see: "✓ Connected to Brother QL-820NWB"
5. Tap "Print" on any order
6. Label should physically print
7. Order is only marked "printed" after successful print

---

## Setting Up GitHub Actions (One-Time Setup)

This enables automatic APK builds every time you update the code.

### Step 1: Get Brother SDK

You need to upload the Brother SDK to your repository (one time only):

1. Download Brother Print SDK from: https://support.brother.com/g/s/es/dev/en/mobilesdk/android/index.html
2. Extract the zip file
3. Find `BrotherPrintLibrary.aar`
4. Copy it to: `midpack2-android-wrapper/android/app/libs/BrotherPrintLibrary.aar`

### Step 2: Commit and Push

```bash
cd /home/wesley/clawd/midpack2-android-wrapper
git add .
git commit -m "Add Brother SDK and GitHub Actions workflow"
git push
```

### Step 3: Enable GitHub Actions

1. Go to your repository on GitHub
2. Click "Actions" tab
3. Enable workflows if prompted
4. The workflow will automatically trigger on push
5. Wait for the build to complete (~5-10 minutes)

### Step 4: Download APK

1. Click on the completed workflow run
2. Scroll to "Artifacts" section
3. Download `midpack2-debug-apk`
4. Extract the APK file

---

## Manual Build (If You Have Android Studio on a Computer)

If you have access to a computer with Android Studio:

1. Download Brother SDK (see above)
2. Place `BrotherPrintLibrary.aar` in `android/app/libs/`
3. Open `midpack2-android-wrapper/android/` in Android Studio
4. Click Build → Build Bundle(s) / APK(s) → Build APK(s)
5. APK will be at: `android/app/build/outputs/apk/debug/app-debug.apk`
6. Transfer to tablet and install

---

## Deployment Checklist (Per Tablet)

For each kitchen location:

- [ ] Download latest APK from GitHub Actions
- [ ] Install APK on tablet
- [ ] Enable "Install from Unknown Sources"
- [ ] Power on Brother QL-820NWB printer
- [ ] Pair printer via Bluetooth
- [ ] Open Midpack 2 app
- [ ] Test printer connection
- [ ] Print test label
- [ ] Verify label prints correctly
- [ ] Verify order marked as "printed" after successful print

---

## Updating the App

When you need to update the app on tablets:

1. Make code changes on your computer
2. Push to GitHub
3. GitHub Actions automatically builds new APK
4. Download new APK
5. Install on tablets (will update existing app)

**No need to uninstall first** - Android handles updates automatically when you install a newer version.

---

## Kitchen Locations

Deploy to these tablets:

- [ ] K1 Avondale - Chicago
- [ ] K2 South Loop - Chicago
- [ ] K3 Milwaukee - Wisconsin
- [ ] K4 Commerce - Dallas
- [ ] K5 Fort Worth - Texas
- [ ] K6 West Plano - Texas

---

## Troubleshooting

### "App not installed"
- Make sure "Unknown Sources" is enabled
- Try uninstalling old version first
- Check APK is not corrupted

### "Brother SDK not found" build error
- The Brother SDK AAR file must be in the repository
- Check: `android/app/libs/BrotherPrintLibrary.aar` exists
- Re-run setup-brother-sdk.sh script

### Printer won't connect
- Check Bluetooth is enabled on tablet
- Verify printer is paired in Bluetooth settings
- Try forgetting and re-pairing the device
- Check printer is powered on and in range

### Prints don't complete
- Check printer has paper loaded
- Check printer cover is closed
- Check for paper jams
- Verify printer is not in error state

### Orders marked "printed" too early
- This was the bug we just fixed!
- Make sure you're using the latest version with async callback integration
- Old version marked orders printed immediately
- New version waits for physical print to complete

---

## Support

For issues:
1. Check tablet logs (if accessible)
2. Check printer status lights
3. Try reprinting from the "Printed" section (can undo and retry)
4. Restart app if needed
5. Restart printer if needed
6. Re-pair Bluetooth connection if needed
