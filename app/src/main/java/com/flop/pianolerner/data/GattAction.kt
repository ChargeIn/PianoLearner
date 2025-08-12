/*
 * Copyright (c) Florian Plesker
 * florian.plesker@web.de
 */

package com.flop.pianolerner.data

import android.bluetooth.BluetoothGattCharacteristic

open class GattAction

class GattReadAction(
    val characteristic: BluetoothGattCharacteristic,
    val callback: (value: String) -> Unit
) :
    GattAction()