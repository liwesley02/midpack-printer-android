package com.happykitchens.midpack2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.brother.ptouch.sdk.NetPrinter;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.PrinterStatus;
import com.getcapacitor.Bridge;
import com.getcapacitor.BridgeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends BridgeActivity {
    private static final String TAG = "MidpackPrint";
    private static final int BLUETOOTH_PERMISSION_REQUEST_CODE = 1001;

    private Printer brotherPrinter;
    private ExecutorService executorService;
    private Handler mainHandler;
    private String connectedPrinterAddress;
    private String pendingCallbackId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        configureMidpackBridge();

        // Request Bluetooth permissions on startup
        requestBluetoothPermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
        if (brotherPrinter != null) {
            brotherPrinter.endCommunication();
        }
    }

    private void requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+ requires BLUETOOTH_CONNECT and BLUETOOTH_SCAN
            List<String> permissionsNeeded = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH_CONNECT);
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH_SCAN);
            }

            if (!permissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    permissionsNeeded.toArray(new String[0]),
                    BLUETOOTH_PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                Log.d(TAG, "Bluetooth permissions granted");
                // If there was a pending connection attempt, retry it
                if (pendingCallbackId != null) {
                    String callbackId = pendingCallbackId;
                    pendingCallbackId = null;
                    new AndroidPrintBridge().connectBrotherPrinter(callbackId);
                }
            } else {
                Log.w(TAG, "Bluetooth permissions denied");
                if (pendingCallbackId != null) {
                    new AndroidPrintBridge().notifyCallback(
                        pendingCallbackId,
                        false,
                        "Bluetooth permissions denied. Please grant permissions in Settings."
                    );
                    pendingCallbackId = null;
                }
            }
        }
    }

    private boolean hasBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Pre-Android 12 doesn't need runtime permissions
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configureMidpackBridge() {
        Bridge bridge = getBridge();
        if (bridge == null) return;

        WebView webView = bridge.getWebView();
        if (webView == null) return;

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        webView.addJavascriptInterface(new AndroidPrintBridge(), "AndroidPrintBridge");
    }

    public class AndroidPrintBridge {
        @JavascriptInterface
        public boolean isAvailable() {
            return true;
        }

        @JavascriptInterface
        public void connectBrotherPrinter(final String callbackId) {
            // Check permissions first
            if (!hasBluetoothPermissions()) {
                Log.w(TAG, "Bluetooth permissions not granted, requesting...");
                pendingCallbackId = callbackId;
                mainHandler.post(() -> requestBluetoothPermissions());
                return;
            }

            executorService.execute(() -> {
                try {
                    Log.d(TAG, "Starting Brother printer discovery...");

                    // Initialize printer if needed
                    if (brotherPrinter == null) {
                        brotherPrinter = new Printer();
                        PrinterInfo printerInfo = brotherPrinter.getPrinterInfo();
                        printerInfo.printerModel = PrinterInfo.Model.QL_820NWB;
                        printerInfo.port = PrinterInfo.Port.BLUETOOTH;
                        printerInfo.paperSize = PrinterInfo.PaperSize.CUSTOM;
                        brotherPrinter.setPrinterInfo(printerInfo);
                    }

                    // Scan for Bluetooth devices
                    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (btAdapter == null) {
                        notifyCallback(callbackId, false, "Bluetooth not supported on this device");
                        return;
                    }

                    if (!btAdapter.isEnabled()) {
                        notifyCallback(callbackId, false, "Bluetooth is disabled. Please enable Bluetooth in Settings.");
                        return;
                    }

                    Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
                    BluetoothDevice brotherDevice = null;

                    for (BluetoothDevice device : pairedDevices) {
                        String name = device.getName();
                        if (name != null && name.contains("Brother")) {
                            brotherDevice = device;
                            Log.d(TAG, "Found Brother printer: " + name + " (" + device.getAddress() + ")");
                            break;
                        }
                    }

                    if (brotherDevice == null) {
                        notifyCallback(callbackId, false, "No Brother printer found in paired devices. Please pair the printer in Bluetooth settings first.");
                        return;
                    }

                    // Update printer info with Bluetooth address
                    PrinterInfo printerInfo = brotherPrinter.getPrinterInfo();
                    printerInfo.macAddress = brotherDevice.getAddress();
                    brotherPrinter.setPrinterInfo(printerInfo);

                    // Test connection
                    boolean connected = brotherPrinter.startCommunication();
                    if (connected) {
                        connectedPrinterAddress = brotherDevice.getAddress();
                        brotherPrinter.endCommunication();
                        notifyCallback(callbackId, true, "Connected to " + brotherDevice.getName());
                        showToast("✓ Connected to " + brotherDevice.getName());
                    } else {
                        notifyCallback(callbackId, false, "Failed to connect to printer. Make sure the printer is on and in range.");
                    }

                } catch (SecurityException e) {
                    Log.e(TAG, "Security exception - missing Bluetooth permission", e);
                    notifyCallback(callbackId, false, "Bluetooth permission required. Please grant in Settings.");
                } catch (Exception e) {
                    Log.e(TAG, "Error connecting to printer", e);
                    notifyCallback(callbackId, false, "Error: " + e.getMessage());
                }
            });
        }

        @JavascriptInterface
        public void printBrotherLabelJson(String payload, final String callbackId) {
            executorService.execute(() -> {
                try {
                    JSONObject json = new JSONObject(payload);
                    String orderNumber = json.optString("orderNumber", "");
                    String customerName = json.optString("customerName", "Guest");
                    String platform = json.optString("platform", "");
                    int itemCount = json.optInt("itemCount", 0);

                    Log.d(TAG, "Print request for order #" + orderNumber);

                    if (brotherPrinter == null || connectedPrinterAddress == null) {
                        notifyCallback(callbackId, false, "Printer not connected. Please connect first.");
                        return;
                    }

                    // Start communication
                    boolean connected = brotherPrinter.startCommunication();
                    if (!connected) {
                        notifyCallback(callbackId, false, "Failed to connect to printer");
                        return;
                    }

                    // Create label content
                    StringBuilder labelText = new StringBuilder();
                    labelText.append("Order #").append(orderNumber).append("\n");
                    labelText.append(customerName).append("\n");
                    if (!platform.isEmpty()) {
                        labelText.append(platform).append("\n");
                    }
                    if (itemCount > 0) {
                        labelText.append(itemCount).append(" item").append(itemCount > 1 ? "s" : "").append("\n");
                    }

                    // Print the label
                    PrinterStatus status = brotherPrinter.printText(labelText.toString());
                    brotherPrinter.endCommunication();

                    if (status.errorCode == PrinterInfo.ErrorCode.ERROR_NONE) {
                        Log.d(TAG, "Print successful for order #" + orderNumber);
                        notifyCallback(callbackId, true, "✓ Printed #" + orderNumber);
                        showToast("✓ Printed #" + orderNumber);
                    } else {
                        String error = "Print error: " + status.errorCode;
                        Log.e(TAG, error);
                        notifyCallback(callbackId, false, error);
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Invalid print payload", e);
                    notifyCallback(callbackId, false, "Invalid payload: " + e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, "Print error", e);
                    notifyCallback(callbackId, false, "Error: " + e.getMessage());
                }
            });
        }

        @JavascriptInterface
        public void printBrotherLabel(String payload, String callbackId) {
            printBrotherLabelJson(payload, callbackId);
        }

        private void notifyCallback(final String callbackId, final boolean success, final String message) {
            mainHandler.post(() -> {
                try {
                    Bridge bridge = getBridge();
                    if (bridge != null && bridge.getWebView() != null) {
                        String js = String.format(
                            "if (window.printCallbacks && window.printCallbacks['%s']) { " +
                            "  window.printCallbacks['%s'](%b, %s); " +
                            "  delete window.printCallbacks['%s']; " +
                            "}",
                            callbackId,
                            callbackId,
                            success,
                            escapeJson(message),
                            callbackId
                        );
                        bridge.getWebView().evaluateJavascript(js, null);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error notifying callback", e);
                }
            });
        }

        private String escapeJson(String str) {
            return "\"" + str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
        }

        private void showToast(final String message) {
            mainHandler.post(() ->
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show()
            );
        }
    }
}
