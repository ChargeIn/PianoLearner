package com.flop.pianolerner.data

import android.Manifest
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import androidx.annotation.RequiresPermission
import java.util.UUID

class GattCallQueue(private val gatt: BluetoothGatt) {
    private val queue = mutableListOf<GattAction>()
    private var isRunning = false;

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun readCharacteristic(serviceUUID: UUID, charUUID: UUID) {
        val service = this.gatt.getService(serviceUUID);
        val characteristic = service?.getCharacteristic(charUUID) ?: return

        if (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_READ != 0) {
            this.queue.add(GattReadAction(characteristic, {}))
            this.startQueue();
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun startQueue() {
        if (this.isRunning || this.queue.size == 0) {
            return
        }

        this.isRunning = true;
        val nextAction = this.queue.removeFirst()
        this.handleAction(nextAction)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun handleAction(action: GattAction) {
        if (action is GattReadAction) {
            this.gatt.readCharacteristic(action.characteristic)
        }
    }
}