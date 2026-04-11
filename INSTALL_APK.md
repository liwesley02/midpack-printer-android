# Installing the Midpack 2 Android App with Brother Printer Support

## Step 1: Wait for Build to Complete

GitHub Actions is now building your APK automatically. The build takes about 5-10 minutes.

Check build status: https://github.com/liwesley02/emma-brain/actions

## Step 2: Download the APK

Once the build completes:

1. Go to https://github.com/liwesley02/emma-brain/actions
2. Click on the most recent "Build Android APK" workflow run
3. Scroll down to "Artifacts"
4. Click "midpack2-debug-apk" to download
5. Extract the ZIP file to get `app-debug.apk`

## Step 3: Transfer to Tablet

**Option A: Direct download on tablet**
1. Open browser on tablet
2. Go to the Actions page on your tablet
3. Download the artifact directly

**Option B: Transfer via USB**
1. Connect tablet to computer via USB
2. Copy `app-debug.apk` to tablet's Download folder
3. Disconnect tablet

**Option C: Email/Cloud**
1. Email the APK to yourself
2. Open email on tablet and download attachment

## Step 4: Install on Tablet

1. On tablet, open File Manager or Downloads app
2. Tap on `app-debug.apk`
3. If prompted, enable "Install from Unknown Sources" for that app
4. Tap "Install"
5. Wait for installation to complete
6. Tap "Open"

## Step 5: Pair Brother Printer (if not already done)

1. Go to Android Settings → Bluetooth
2. Make sure Bluetooth is ON
3. Press and hold Bluetooth button on Brother QL-820NWB until blue light flashes
4. Tap "Brother QL-820NWB" in Available devices
5. Wait for "Paired" status

## Step 6: Test Printing

1. Open the Midpack 2 app
2. Select your restaurant location (K5 Fort Worth)
3. Go to Labels page
4. Tap "Connect Android Printer" button
5. Should connect automatically to paired printer
6. Try printing a test label

## Troubleshooting

### "App not installed" error
- Make sure you enabled "Install from Unknown Sources"
- Try uninstalling any previous version first

### Printer won't connect
- Make sure printer is paired in Bluetooth settings first
- Make sure printer is turned on
- Try forgetting and re-pairing the printer

### Build not showing in Actions
- Check that you're looking at https://github.com/liwesley02/emma-brain/actions
- The workflow might still be running (check for yellow/orange status)

## What This App Does

This native Android app:
- ✓ Uses Brother Print SDK for reliable Bluetooth printing
- ✓ Connects directly to Brother QL-820NWB without browser limitations
- ✓ Prints 62mm x 29mm labels for orders
- ✓ Auto-advances print queue with Space bar shortcut
- ✓ Shows live orders from your selected restaurant

## Next Steps

Once printing works on one tablet, repeat Steps 2-6 for each additional kitchen tablet:
- K1 Avondale tablet
- K2 South Loop tablet
- K3 Milwaukee tablet
- K4 Commerce tablet
- K5 Fort Worth tablet (your current one)
- K6 West Plano tablet
