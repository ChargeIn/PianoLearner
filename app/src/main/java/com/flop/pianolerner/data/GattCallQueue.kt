/*
 * Copyright (c) Florian Plesker
 * florian.plesker@web.de
 */

package com.flop.pianolerner.data

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import java.util.UUID

class GattCallQueue @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT) constructor(
    context: Context,
    device: BluetoothDevice
) : BluetoothGattCallback() {
    private val queue = mutableListOf<GattAction>()
    private var isRunning = false
    private var gatt: BluetoothGatt? = null

    init {
        device.connectGatt(context, false, this);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun readCharacteristic(serviceUUID: UUID, charUUID: UUID, callback: (value: String) -> Unit) {
        if (this.gatt == null) {
            return
        }

        val service = this.gatt!!.getService(serviceUUID);
        val characteristic = service?.getCharacteristic(charUUID) ?: return

        if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_READ != 0) {
            this.queue.add(GattReadAction(characteristic, callback))
            this.startQueue();
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun startQueue() {
        if (this.gatt == null) {
            return
        }

        if (this.isRunning || this.queue.size == 0) {
            return
        }

        this.isRunning = true;
        val nextAction = this.queue.removeFirst()
        this.handleAction(nextAction)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun handleAction(action: GattAction) {
        if (action is GattReadAction) {
            this.gatt!!.readCharacteristic(action.characteristic)
        }
    }

    /**
     * -------------------------
     * GattCallback functions
     * -------------------------
     */

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {

        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                this@GattCallQueue.gatt = gatt

                if (this.queue.size != 0) {
                    this.startQueue()
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                this@GattCallQueue.gatt = null;
                gatt.close()
            }
        } else {
            // Error encountered
            this@GattCallQueue.gatt = null;
            gatt.close()
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        if (gatt != null) {
            this.printGattTable(gatt)
        }
    }

    /**
     * -------------------------
     * Debug functions
     * -------------------------
     */

    // mostly for debugging
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun discoverServices() {
        this.gatt?.discoverServices();
    }

    // Debugging for gatt service
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