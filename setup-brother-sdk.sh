#!/bin/bash
# Setup Brother SDK for Android build

set -e

LIBS_DIR="$(dirname "$0")/android/app/libs"
SDK_FILE="$LIBS_DIR/BrotherPrintLibrary.aar"

echo "=== Brother SDK Setup ==="
echo ""

# Create libs directory
mkdir -p "$LIBS_DIR"

# Check if SDK already exists
if [ -f "$SDK_FILE" ]; then
    echo "✓ Brother SDK already installed at: $SDK_FILE"
    ls -lh "$SDK_FILE"
    exit 0
fi

echo "Brother Print SDK is required but not found."
echo ""
echo "To get the SDK:"
echo "1. Visit: https://support.brother.com/g/s/es/dev/en/mobilesdk/android/index.html"
echo "2. Download the Brother Print SDK for Android (v4.x or later)"
echo "3. Extract the zip file"
echo "4. Find 'BrotherPrintLibrary.aar' inside"
echo "5. Place it at: $LIBS_DIR/BrotherPrintLibrary.aar"
echo ""
echo "After placing the file, run this script again to verify."
echo ""
echo "Expected location: $SDK_FILE"

exit 1
