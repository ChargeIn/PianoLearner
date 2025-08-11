package com.flop.pianolerner.data

import android.bluetooth.BluetoothGattCharacteristic

open class GattAction

class GattReadAction(val characteristic: BluetoothGattCharacteristic, val callback: () -> Unit) :
    GattAction()