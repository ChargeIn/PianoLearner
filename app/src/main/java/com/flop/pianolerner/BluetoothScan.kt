package com.flop.pianolerner

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.flop.pianolerner.data.BLDevicesViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

val LOCATION_PERMISSIONS = listOf(
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION,
)

val BLUETOOTH_PERMISSIONS = listOf(
    Manifest.permission.BLUETOOTH,
    Manifest.permission.BLUETOOTH_SCAN,
)

// https://developer.android.com/reference/android/media/midi/package-summary#btle_scan_devices
val BLE_MIDI_SERVICE_UUID = "03B80E5A-EDE8-4B33-A751-6CE34EC4C700";

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothScan(
    modifier: Modifier = Modifier,
    devicesViewModel: BLDevicesViewModel
) {
    val context = LocalContext.current

//    var bluetoothManager = ContextCompat.getSystemService(BluetoothManager::class);

    val bluetoothPermission =
        rememberMultiplePermissionsState(permissions = BLUETOOTH_PERMISSIONS)
    val locationPermission = rememberMultiplePermissionsState(permissions = LOCATION_PERMISSIONS)


    /** Enables bluetooth if not active */
    val enableBluetooth = remember {
        enableBluetooth@{
            if (!bluetoothPermission.allPermissionsGranted) {
                return@enableBluetooth
            }

            if (!locationPermission.allPermissionsGranted) {
                return@enableBluetooth
            }

            context.startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
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
        Text("Scan for Bluetooth Devices")
        Button(
            onClick = {
                devicesViewModel.setScanningState(true);
                locationPermission.launchMultiplePermissionRequest()
                bluetoothPermission.launchMultiplePermissionRequest()
            }, modifier = modifier
        ) {
            if (devicesViewModel.scanning) {
                Text("Cancel")
            } else {
                Text("Scan")
            }
        }
    }
}