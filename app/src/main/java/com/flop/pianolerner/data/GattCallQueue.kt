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
import com.flop.pianolerner.showToast
import java.util.UUID

class GattCallQueue @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT) constructor(
    private var context: Context,
    device: BluetoothDevice
) : BluetoothGattCallback() {
    private val queue = mutableListOf<GattAction>()
    private var isRunning = false
    private var servicesDiscovered = false;
    private var gatt: BluetoothGatt? = null;

    init {
        device.connectGatt(context, false, this);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun readCharacteristic(serviceUUID: UUID, charUUID: UUID, callback: (value: String) -> Unit) {
        this.queue.add(GattReadAction(serviceUUID, charUUID, callback))
        this.startQueue();
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun startQueue() {
        if (this.gatt == null || !this.servicesDiscovered) {
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
            val service = this.gatt!!.getService(action.service);
            val characteristic = service?.getCharacteristic(action.characteristic);

            if (characteristic == null) {
                showToast(
                    context,
                    "Could not find service or characteristic with ids: ${action.service} ${action.characteristic}"
                );
                return;
            }

            if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_READ != 0) {
                this.gatt!!.readCharacteristic(characteristic);
            } else {
                showToast(
                    context,
                    "Could not read device characteristic: ${action.characteristic}"
                );
            }
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
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                this@GattCallQueue.gatt = null;
                gatt.close()
                showToast(context, "Device connection closed");
            }
        } else {
            // Error encountered
            this@GattCallQueue.gatt = null;
            gatt.close()
            showToast(context, "Gatt Connection closed with error code: $status");
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        this.servicesDiscovered = true;

        if (gatt != null) {
            this.startQueue();
        }
    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray,
        status: Int
    ) {
        val uuid = characteristic.uuid
        when (status) {
            BluetoothGatt.GATT_SUCCESS -> {
                Log.i(
                    "BluetoothGattCallback",
                    "Read characteristic $uuid:n${
                        value.joinToString(
                            separator = " ",
                            prefix = "0x"
                        ) { String.format("%02X", it) }
                    }"
                )
            }

            BluetoothGatt.GATT_READ_NOT_PERMITTED -> {
                showToast(context, "Read not permitted for $uuid!");
            }

            else -> {
                showToast(context, "Gatt Characteristic read failed for $uuid, error: $status");
            }
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