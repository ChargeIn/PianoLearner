package com.flop.pianolerner.data

import android.bluetooth.le.ScanResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class BLDevicesViewModel : ViewModel() {
    var scanning by mutableStateOf(false);
    var error by mutableStateOf("");

    val devices = mutableStateListOf<ScanResult>();

    fun setScanningState(loading: Boolean) {
        this.scanning = loading;
    }
}