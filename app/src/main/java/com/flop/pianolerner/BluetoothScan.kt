/*
 * Copyright (c) Florian Plesker
 * florian.plesker@web.de
 */

package com.flop.pianolerner

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.os.ParcelUuid
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flop.pianolerner.data.BLDevicesViewModel
import com.flop.pianolerner.data.ConnectionDialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.util.UUID

val LOCATION_PERMISSIONS = listOf(
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION,
)

val BLUETOOTH_PERMISSIONS = listOf(
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.BLUETOOTH_CONNECT
)

// https://developer.android.com/reference/android/media/midi/package-summary#btle_scan_devices
val BLE_MIDI_SERVICE_UUID = "03B80E5A-EDE8-4B33-A751-6CE34EC4C700"

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothScan(
    modifier: Modifier = Modifier, devicesViewModel: BLDevicesViewModel
) {
    val context = LocalContext.current


    if (devicesViewModel.openConnectionDialog) {
        ConnectionDialog(devicesViewModel, { devicesViewModel.openConnectionDialog = false })
    }

    val locationManager = remember {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    val bluetoothManager = remember {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    val blAdapter = bluetoothManager.adapter
    var blLeScanner = blAdapter.bluetoothLeScanner


    val bluetoothLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode != RESULT_OK) {
                showToast(
                    context,
                    "Could not enable bluetooth. Please enable manually.",
                )
            } else {
                // scanner is null if bluetooth was disabled
                if (blLeScanner == null) {
                    blLeScanner = blAdapter.bluetoothLeScanner;
                }

                scanMIDIDevices(context, blLeScanner, devicesViewModel)
            }
        }

    val bluetoothPermission =
        rememberMultiplePermissionsState(permissions = BLUETOOTH_PERMISSIONS) { permissions ->
            val allGranted = !permissions.values.contains(false)

            if (allGranted) {
                bluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            } else {
                showToast(
                    context,
                    "Could not enable bluetooth permissions. Please enable manually.",
                )
            }
        }

    val locationLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (!locationManager.isLocationEnabled) {
                showToast(
                    context,
                    "Could not enable gps. Please enable manually.",
                )
            } else {
                bluetoothPermission.launchMultiplePermissionRequest()
            }
        }

    val locationPermission =
        rememberMultiplePermissionsState(permissions = LOCATION_PERMISSIONS) { permissions ->
            val allGranted = !permissions.values.contains(false)

            if (allGranted) {
                // check if gps is enabled
                if (locationManager.isLocationEnabled) {
                    bluetoothPermission.launchMultiplePermissionRequest()
                } else {
                    AlertDialog.Builder(context)
                        .setTitle("Grant location access")
                        .setMessage("Please switch on your gps to show devices near your location.")
                        .setPositiveButton("Settings") { _: DialogInterface, _: Int ->
                            locationLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        }
                        .show()
                }
            } else {
                showToast(
                    context,
                    "Could not enable location permissions. Please enable manually.",
                )
            }
        }

    if (devicesViewModel.scanning) {
        LinearProgressIndicator(modifier = modifier.fillMaxWidth())
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (devicesViewModel.devices.size > 0) {
            LazyColumn(modifier = Modifier.weight(1F)) {
                items(devicesViewModel.devices) { scanResult ->
                    DeviceItem(scanResult, modifier) {
                        devicesViewModel.connectToDevice(context, scanResult.device)
                    }
                }
            }
        } else {
            Text("Scan for Bluetooth Devices")
        }

        Button(
            onClick = {
                if (devicesViewModel.scanning) {
                    stopScan(devicesViewModel, blLeScanner)
                } else {
                    locationPermission.launchMultiplePermissionRequest()
                }
            },
            modifier = modifier.padding(8.dp)
        ) {
            if (devicesViewModel.scanning) {
                Text("Cancel")
            } else {
                Text("Scan")
            }
        }
    }
}

fun handleScanError(context: Context, errorCode: Number) {
    if (errorCode == ScanCallback.SCAN_FAILED_ALREADY_STARTED) {
        showToast(context, "Bluetooth Scan Failed: Scan already started.")
        return
    }

    if (errorCode == ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED) {
        showToast(
            context,
            "Bluetooth Scan Failed: Could not register application.",
        )
        return
    }

    if (errorCode == ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED) {
        showToast(context, "Bluetooth Scan Failed: Not Supported.")
        return
    }

    if (errorCode == ScanCallback.SCAN_FAILED_INTERNAL_ERROR) {
        showToast(context, "Bluetooth Scan failed: Internal Server Error.")
        return
    }
}

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
fun scanMIDIDevices(
    context: Context,
    bluetoothLeScanner: BluetoothLeScanner,
    devicesViewModel: BLDevicesViewModel
) {
    val scanCallback: ScanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            result?.let {
                if (!devicesViewModel.devices.any { r -> r.device.address == it.device.address } && result.isConnectable) {
                    devicesViewModel.devices.add(it)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)

            devicesViewModel.setScanningState(false)
            handleScanError(context, errorCode)
        }
    }

    devicesViewModel.setScanningState(true)

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

@RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
fun stopScan(devicesViewModel: BLDevicesViewModel, bluetoothLeScanner: BluetoothLeScanner) {
    if (!devicesViewModel.scanning) {
        return
    }

    devicesViewModel.setScanningState(false);
    bluetoothLeScanner.stopScan(object : ScanCallback() {});
}

@Composable()
private fun DeviceItem(
    scanResult: ScanResult,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ListItem(
        leadingContent = {
            Text(
                text = scanResult.scanRecord?.deviceName ?: scanResult.device.address,
                fontSize = 16.sp,
                modifier = modifier,
            )
        },
        headlineContent = {
            Text(scanResult.device.address)
        },
        trailingContent = {
            Button(onClick = onClick) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Connect to Device"
                )
            }

        },
    )

}