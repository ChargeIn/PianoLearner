/*
 * Copyright (c) Florian Plesker
 * florian.plesker@web.de
 */

package com.flop.pianolerner.data

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.le.ScanResult
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.UUID

// Service IDs (https://www.bluetooth.com/specifications/assigned-numbers/)
// BASE_UUID = 00000000-0000-1000-8000-00805f9b34fb

// GAP Service: 00001800
// Characteristics by UUID:
//  - 00002a00 = Device Name
//  - 00002a01 = Appearance
//  - 00002a04 = Peripheral Preferred Connection Parameters
//  - 00002a04 = Alert Level
//  - 00002aa6 = Central Address Resolution

// GATT Service: 00001801
// No Characteristics

// MIDI (https://learn.sparkfun.com/tutorials/midi-ble-tutorial/all)
// Service: 03b80e5a-ede8-4b33-a751-6ce34ec4c700 = MIDI Service
// Characteristics by UUID:
//  - 7772e5db-3868-4112-a1a9-f2669d106bf3 = MIDI Characteristic

// Device Information Service: 0000180a-0000-1000-8000-00805f9b34fb
// Characteristics by UUID:
//  - 00002a29 = Manufacturer Name String
//  - 00002a24 = Model Number String
//  - 00002a25 = Serial Number String
//  - 00002a26 = Firmware Revision String
//  - 00002a27 = Hardware Revision String
//  - 00002a28 = Software Revision String


class BLDevicesViewModel : ViewModel() {
    var scanning by mutableStateOf(false)
    var error by mutableStateOf("")
    var queue: GattCallQueue? = null

    val devices = mutableStateListOf<ScanResult>();
    var connectedDevice by mutableStateOf<BluetoothGatt?>(null);

    fun setScanningState(loading: Boolean) {
        this.scanning = loading
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connectToDevice(context: Context, device: BluetoothDevice?) {
        if (device != null) {
            this.queue = GattCallQueue(context, device)

            val gabService = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb")
            val deviceNameUUID = UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb");
            this.queue!!.readCharacteristic(gabService, deviceNameUUID);
        }
    }
}