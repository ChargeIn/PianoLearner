package org.flop.plugin.android.piano_connector

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.location.LocationManager
import android.os.Build
import android.os.ParcelUuid
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import org.godotengine.godot.utils.PermissionsUtil
import java.util.UUID

const val bluetoothHandlerState = "bluetoothHandler"

enum class BluetoothScanState(val state: String) {
    STARTED("started"),
    LOCATION_DISABLED("locationDisabled"),
    BLUETOOTH_DISABLED("bluetoothDisabled"),
    NEW_DEVICES("newDevices"),
    STOPPED("stopped"),
}

// https://developer.android.com/reference/android/media/midi/package-summary#btle_scan_devices
const val BLE_MIDI_SERVICE_UUID = "03B80E5A-EDE8-4B33-A751-6CE34EC4C700"

class BluetoothHandler(val plugin: PianoConnectorAndroidPlugin, val activity: Activity) {
    private val TAG: String = PermissionsUtil::class.java.simpleName

    private var scanning = false


    val locationManager =
        this.activity.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    val bluetoothManager =
        this.activity.applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?

    val devices = mutableListOf<ScanResult>()

    val PERMISSION_REQUEST_CODE = 10001

    @RequiresApi(Build.VERSION_CODES.S)
    fun onRequestPermissionsResult(
        permissions: Array<out String?>?,
        grantResults: IntArray?
    ) {
        if (grantResults == null) {
            return
        }

        if (grantResults.indexOf(0) != -1) {
            return
        }

        this.startScan()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun startScan() {
        this.activity.runOnUiThread {
            this.requestNeededPermissions()
        }
    }

    fun stopScan() {
        this.scanning = false
        emitSignal(BluetoothScanState.STOPPED)
    }

    fun emitSignal(state: BluetoothScanState) {
        this.plugin.emitSignal(bluetoothHandlerState, state.state)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun requestNeededPermissions() {
        val hasPermissions = this.requestPermissions(
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            ),
        )

        if (hasPermissions) {
            this.checkLocationEnabled()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun checkLocationEnabled() {
        this.locationManager?.isLocationEnabled?.let {
            if (!it) {
                this.emitSignal(BluetoothScanState.LOCATION_DISABLED)
            } else {
                this.checkBluetoothEnabled()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun checkBluetoothEnabled() {
        this.bluetoothManager?.adapter?.isEnabled?.let {
            if (!it) {
                this.emitSignal(BluetoothScanState.BLUETOOTH_DISABLED)
            } else {
                this.initScan()
            }
        }
    }

    fun enableBluetooth() {

    }

    fun enableLocation() {
        this.activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    /**
     * Request a list of dangerous permissions. The requested permissions must be included in the app's AndroidManifest
     * @param permissions list of the permissions to request.
     * @param activity the caller activity for this method.
     * @return true/false. "true" if permissions are already granted, "false" if a permissions request was dispatched.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermissions(permissions: List<String>): Boolean {
        if (permissions.isEmpty()) {
            return true
        }

        var dispatchedPermissionsRequest = false
        val requestedPermissions: MutableSet<String?> = HashSet()

        for (permission in permissions) {
            try {
                val permissionInfo: PermissionInfo = getPermissionInfo(this.activity, permission)

                val protectionLevel =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) permissionInfo.protection else permissionInfo.protectionLevel

                if (
                    (protectionLevel and PermissionInfo.PROTECTION_DANGEROUS) == PermissionInfo.PROTECTION_DANGEROUS
                    && ContextCompat.checkSelfPermission(
                        this.activity,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d(TAG, "Requesting permission " + permission)
                    requestedPermissions.add(permission)
                }
            } catch (e: PackageManager.NameNotFoundException) {
                // Skip this permission and continue.
                Log.w(TAG, "Unable to identify permission " + permission, e)
            }
        }

        if (!requestedPermissions.isEmpty()) {
            this.activity.requestPermissions(
                requestedPermissions.toTypedArray<String?>(), this.PERMISSION_REQUEST_CODE
            )

            dispatchedPermissionsRequest = true
        }

        return !dispatchedPermissionsRequest
    }

    /**
     * Returns the information of the desired permission.
     * @param context the caller context for this method.
     * @param permission the name of the permission.
     * @return permission info object
     * @throws PackageManager.NameNotFoundException the exception is thrown when a given package, application, or component name cannot be found.
     */
    @Throws(PackageManager.NameNotFoundException::class)
    private fun getPermissionInfo(context: Context, permission: String): PermissionInfo {
        val packageManager = context.packageManager
        return packageManager.getPermissionInfo(permission, 0)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initScan() {
        val bluetoothLeScanner = bluetoothManager?.adapter?.bluetoothLeScanner

        // safety check should not occur
        if (bluetoothLeScanner == null) {
            showToast("Could not load bluetooth service.")
            return
        }

        this.scanning = true
        this.emitSignal(BluetoothScanState.STARTED)

        val scanCallback: ScanCallback = object : ScanCallback() {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)

                result?.let {
                    if (!devices.any { r -> r.device.address == it.device.address } && result.isConnectable) {
                        devices.add(it)
                        emitSignal(BluetoothScanState.NEW_DEVICES)
                    }
                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)

                stopScan()
                handleScanError(errorCode)
            }
        }

        val serviceUuid = UUID.fromString(BLE_MIDI_SERVICE_UUID)
        val scanFilterBuilder = ScanFilter.Builder()
        val scanFilter = scanFilterBuilder.setServiceUuid(ParcelUuid(serviceUuid)).build()

        val scanFilters: MutableList<ScanFilter> = ArrayList()
        scanFilters.add(scanFilter)

        val scanSettings =
            ScanSettings.Builder()
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()

        bluetoothLeScanner.startScan(scanFilters, scanSettings, scanCallback)
    }

    fun handleScanError(errorCode: Number) {
        if (errorCode == ScanCallback.SCAN_FAILED_ALREADY_STARTED) {
            showToast("Bluetooth Scan Failed: Scan already started.")
            return
        }

        if (errorCode == ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED) {
            showToast("Bluetooth Scan Failed: Could not register application.")
            return
        }

        if (errorCode == ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED) {
            showToast("Bluetooth Scan Failed: Not Supported.")
            return
        }

        if (errorCode == ScanCallback.SCAN_FAILED_INTERNAL_ERROR) {
            showToast("Bluetooth Scan failed: Internal Server Error.")
            return
        }
    }


    fun showToast(message: String) {
        Toast.makeText(activity.applicationContext, message, Toast.LENGTH_SHORT).show()
    }
}