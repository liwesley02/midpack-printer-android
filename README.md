# Midpack 2 Android Wrapper

Isolated Android wrapper for `https://www.happykitchens.io/midpack2`.

## What it does
- Opens the live Midpack 2 page in a Capacitor Android WebView
- Injects `window.AndroidPrintBridge`
- Exposes these web-callable methods:
  - `isAvailable()`
  - `connectBrotherPrinter()`
  - `printBrotherLabelJson(payload)`
  - `printBrotherLabel(payload)`

## Current state
- Wrapper project scaffolded
- Android WebView bridge injected in `MainActivity.java`
- Web side in the dashboard already calls this bridge when present
- Native Brother SDK printing is **not wired yet**

## Important files
- `capacitor.config.ts`
- `android/app/src/main/java/com/happykitchens/midpack2/MainActivity.java`

## Next native step
Replace the Toast-based stubs in `AndroidPrintBridge` with Brother QL-820NWB SDK logic for:
1. Bluetooth device discovery / reconnect
2. Printer selection persistence
3. Actual raster/label print job submission

## Build
```bash
npm install
npm run build
npx cap sync android
cd android && ./gradlew assembleDebug
```
