#!/usr/bin/env python3
"""
Download Brother Print SDK by programmatically accepting the license agreement.
"""
import requests
import sys

def download_brother_sdk():
    # The agreement URL
    url = "https://support.brother.com/g/b/agreement.aspx?dlid=dlfp100900_000"

    print("Fetching Brother SDK download page...")
    session = requests.Session()

    # First, get the agreement page to establish session
    response = session.get(url, allow_redirects=True)
    print(f"Status: {response.status_code}")

    # The actual download URL (from the agreement page form)
    download_url = "https://support.brother.com/g/b/downloadlist.aspx?c=us&lang=en&prod=lpql820nwbus&dlid=dlfp100900_000"

    print("Attempting direct download...")
    response = session.get(download_url, allow_redirects=True)

    # Check if we got a zip file
    content_type = response.headers.get('content-type', '')
    print(f"Content-Type: {content_type}")
    print(f"Size: {len(response.content)} bytes")

    if 'zip' in content_type or len(response.content) > 1000000:  # > 1MB likely a real file
        with open('brother-sdk.zip', 'wb') as f:
            f.write(response.content)
        print(f"✓ Downloaded Brother SDK ({len(response.content)} bytes)")
        return True
    else:
        print("✗ Download failed - got HTML page instead of ZIP")
        print("The SDK requires manual download from Brother's website.")
        return False

if __name__ == "__main__":
    success = download_brother_sdk()
    sys.exit(0 if success else 1)
