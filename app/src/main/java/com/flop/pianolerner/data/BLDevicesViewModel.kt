package com.flop.pianolerner.data

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class BLDevicesViewModel : ViewModel() {
    var scanning by mutableStateOf(false)
    var error by mutableStateOf("")

    val devices = mutableStateListOf<ScanResult>();
    var connectedDevice by mutableStateOf<BluetoothGatt?>(null);

    fun setScanningState(loading: Boolean) {
        this.scanning = loading
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connectToDevice(context: Context, device: BluetoothDevice) {
        device.connectGatt(context, false, this.gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val deviceAddress = gatt.device.address
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.w("BluetoothGattCallback", "Successfully connected to $deviceAddress")
                    connectedDevice = gatt

                    gatt.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.w("BluetoothGattCallback", "Successfully disconnected from $deviceAddress")
                    gatt.close()
                }
            } else {
                Log.w(
                    "BluetoothGattCallback",
                    "Error $status encountered for $deviceAddress! Disconnecting..."
                )
                gatt.close()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            Log.w("BluetoothGattCallback", "Successfully connected to")
            if (gatt != null) {
                this.printGattTable(gatt)
            }
        }

        private fun printGattTable(gatt: BluetoothGatt) {
            if (gatt.services.isEmpty()) {
                Log.i(
                    "printGattTable",
                    "No service and characteristic available, call discoverServices() first?"
                )
                return
            }
            gatt.services.forEach { service ->
                val characteristicsTable = service.characteristics.joinToString(
                    separator = "n|--",
                    prefix = "|--"
                ) { it.uuid.toString() }
                Log.i(
                    "printGattTable",
                    "nService ${service.uuid}nCharacteristics:n$characteristicsTable"
                )
            }
        }
    }
}