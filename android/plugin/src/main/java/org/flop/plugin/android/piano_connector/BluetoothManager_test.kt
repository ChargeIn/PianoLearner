//import android.Manifest
//import android.bluetooth.BluetoothAdapter
//import android.bluetooth.BluetoothGatt
//import android.bluetooth.BluetoothGattCallback
//import android.bluetooth.BluetoothGattCharacteristic
//import android.bluetooth.BluetoothGattDescriptor
//import android.bluetooth.BluetoothGattService
//import android.bluetooth.BluetoothProfile
//import android.bluetooth.le.BluetoothLeScanner
//import android.bluetooth.le.ScanCallback
//import android.bluetooth.le.ScanResult
//import android.bluetooth.le.ScanSettings
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.content.pm.PackageManager
//import android.location.LocationManager
//import android.os.Build
//import android.os.Handler
//import android.util.ArraySet
//import androidx.annotation.RequiresApi
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import java.util.UUID
//import kotlin.collections.HashMap
//import kotlin.collections.MutableList
//import kotlin.collections.MutableMap
//import kotlin.collections.MutableSet
//import kotlin.collections.get
//import kotlin.collections.mutableListOf
//
//internal class {
//    inner class BluetoothManager(godot: Godot?) {
//        // General
//        private val context: Context
//        private val mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//        private val bluetoothLeScanner: BluetoothLeScanner =
//            mBluetoothAdapter.getBluetoothLeScanner()
//        private val locationManager: LocationManager?
//        private val handler = Handler()
//
//
//        private var settings: ScanSettings? = null
//
//        private val bluetoothGatt: BluetoothGatt? =
//            null // This is a reference to the connected device
//
//        // Specific
//        private var scanning = false
//        private val connected = false
//        private val devices: MutableMap<String?, ScanResult?> =
//            HashMap<String?, ScanResult?>() // Key is the address
//
//        var reportDuplicates: Boolean = true
//
//        // Permissions related functions
//        fun hasLocationPermissions(): Boolean {
//            if (ContextCompat.checkSelfPermission(
//                    context,
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(
//                    context,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                return false
//            } else {
//                return true
//            }
//        }
//
//        private fun hasPermission(permission: String): Boolean {
//            return ActivityCompat.checkSelfPermission(
//                context,
//                permission
//            ) == PackageManager.PERMISSION_GRANTED
//        }
//
//        val pluginName: String
//            get() = "GodotBluetooth344"
//
//        fun hasGetScanPeriod(): Boolean {
//            return true
//        }
//
//        var scanPeriod: Long
//            get() {
//                return.BluetoothManager.Companion.ScanPeriod
//            }
//            set(scanPeriod) {
//                .Companion.ScanPeriod = scanPeriod
//            }
//
//        @get:Suppress("deprecation")
//        val pluginMethods: MutableList<String?>
//            get() = mutableListOf<String?>(
//                "sendDebugSignal",
//                "bluetoothStatus",
//                "scan",
//                "stopScan",
//                "hasLocationPermissions",
//                "locationStatus",
//                "connect",
//                "disconnect",
//                "listServicesAndCharacteristics",
//                "subscribeToCharacteristic",
//                "unsubscribeFromCharacteristic",
//                "writeBytesToCharacteristic",
//                "writeStringToCharacteristic",
//                "readFromCharacteristic",
//                "setScanPeriod",
//                "getScanPeriod",
//                "hasGetScanPeriod",
//                "setReportDuplicates",
//                "getReportDuplicates"
//            )
//
//        fun sendDebugSignal(s: String?) {
//            emitSignal("_on_debug_message", s)
//            Log.d("BluetoothManager", s)
//        }
//
//        fun sendNewDevice(newDevice: ScanResult) {
//            val deviceData: org.godotengine.godot.Dictionary = Dictionary()
//
//            deviceData.put("name", newDevice.getScanRecord()!!.getDeviceName())
//            deviceData.put("address", newDevice.getDevice().getAddress())
//            deviceData.put("rssi", newDevice.getRssi())
//            deviceData.put("manufacturerData", newDevice.getScanRecord()!!.getBytes())
//
//            emitSignal("_on_device_found", deviceData)
//        }
//
//        @get:RequiresApi(api = Build.VERSION_CODES.M)
//        val pluginSignals: MutableSet<SignalInfo>
//            get() {
//                val signals: MutableSet<SignalInfo?> =
//                    ArraySet<SignalInfo?>()
//
//                signals.add(SignalInfo("_on_debug_message", String::class.java))
//                signals.add(
//                    SignalInfo(
//                        "_on_device_found",
//                        org.godotengine.godot.Dictionary::class.java
//                    )
//                )
//                signals.add(SignalInfo("_on_bluetooth_status_change", String::class.java))
//                signals.add(SignalInfo("_on_location_status_change", String::class.java))
//                signals.add(SignalInfo("_on_connection_status_change", String::class.java))
//                signals.add(SignalInfo("_on_characteristic_finding", String::class.java))
//                signals.add(
//                    SignalInfo(
//                        "_on_characteristic_found",
//                        org.godotengine.godot.Dictionary::class.java
//                    )
//                )
//                signals.add(
//                    SignalInfo(
//                        "_on_characteristic_read",
//                        org.godotengine.godot.Dictionary::class.java
//                    )
//                )
//                signals.add(SignalInfo("_on_scan_stopped", String::class.java))
//                return signals
//            }
//
//        fun scan() {
//            if (hasLocationPermissions()) {
//                if (!scanning) {
//                    // Stops scanning after a predefined scan period.
//                    handler.postDelayed(object : Runnable {
//                        override fun run() {
//                            scanning = false
//
//                            if (hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
//                                sendDebugSignal(
//                                    "Cannot stop a scan because you do not have Manifest.permission.BLUETOOTH_SCAN"
//                                )
//                                return
//                            }
//                            bluetoothLeScanner.stopScan(leScanCallback)
//                            emitSignal("_on_scan_stopped", "scanTimedOut")
//                        }
//                    }, . BluetoothManager . Companion . ScanPeriod)
//
//                    scanning = true
//                    bluetoothLeScanner.startScan(null, settings, leScanCallback)
//                }
//            } else {
//                sendDebugSignal("Cannot start a scan because you do not have location permissions")
//            }
//        }
//
//        fun stopScan() {
//            if (scanning) {
//                scanning = false
//                if (hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
//                    sendDebugSignal("Cannot stop a scan because you do not have Manifest.permission.BLUETOOTH_SCAN")
//                    return
//                }
//                //emitSignal("_on_scan_stopped", "stopScan");
//                bluetoothLeScanner.stopScan(leScanCallback)
//            }
//        }
//
//
//        private val leScanCallback: ScanCallback = object : ScanCallback() {
//            override fun onScanResult(callbackType: Int, result: ScanResult?) {
//                // We are only interested in devices with name
//                if (result != null && result.getDevice() != null && result.getDevice()
//                        .getAddress() != null && result.getScanRecord()!!
//                        .getDeviceName() != null
//                ) {
//                    if (!devices.containsKey(result.getDevice().getAddress())) {
//                        devices.put(result.getDevice().getAddress(), result)
//                        sendNewDevice(result)
//                    } else {
//                        if (reportDuplicates) {
//                            sendNewDevice(result)
//                        }
//                    }
//                }
//            }
//        }
//
//        init {
//            // Get the context
//            this.context = getActivity().getApplicationContext()
//
//            // Get the location manager
//            this.locationManager =
//                this.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
//
//            // Register the listener to the Bluetooth Status
//            var filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
//            context.registerReceiver(mReceiver, filter)
//
//
//            // Register the listener to the Location Status
//            filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
//            context.registerReceiver(mGpsSwitchStateReceiver, filter)
//
//            val settingBuilder = ScanSettings.Builder()
//            settingBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
//            settings = settingBuilder.build()
//        }
//
//        companion object {
//            private var ScanPeriod: Long = 100000
//        }
//    }
//
//    // Status functions
//    fun bluetoothStatus(): Boolean {
//        return mBluetoothAdapter.isEnabled()
//    }
//
//    fun locationStatus(): Boolean {
//        var gps_enabled = false
//        var network_enabled = false
//
//        try {
//            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        } catch (ex: Exception) {
//        }
//
//        try {
//            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//        } catch (ex: Exception) {
//        }
//
//        if (!gps_enabled && !network_enabled) {
//            return false
//        }
//        return true
//    }
//
//    fun listServicesAndCharacteristics() {
//        if (connected) {
//            // Discover services and characteristics for this device
//            if (hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
//                sendDebugSignal("Cannot list services because you do not have Manifest.permission.BLUETOOTH_CONNECT")
//
//                return
//            }
//
//            bluetoothGatt.discoverServices()
//        }
//    }
//
//    // This monitors the bluetooth status
//    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent) {
//            val action = intent.getAction()
//
//            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
//                val state = intent.getIntExtra(
//                    BluetoothAdapter.EXTRA_STATE,
//                    BluetoothAdapter.ERROR
//                )
//                when (state) {
//                    BluetoothAdapter.STATE_OFF -> emitSignal("_on_bluetooth_status_change", "off")
//                    BluetoothAdapter.STATE_TURNING_OFF -> emitSignal(
//                        "_on_bluetooth_status_change",
//                        "turning_off"
//                    )
//
//                    BluetoothAdapter.STATE_ON -> emitSignal("_on_bluetooth_status_change", "on")
//                    BluetoothAdapter.STATE_TURNING_ON -> emitSignal(
//                        "_on_bluetooth_status_change",
//                        "turning_on"
//                    )
//                }
//            }
//        }
//    }
//
//    // This monitors the location status
//    private val mGpsSwitchStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent) {
//            val action = intent.getAction()
//
//            if (action == LocationManager.PROVIDERS_CHANGED_ACTION) {
//                val isGpsEnabled: Boolean =
//                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//                val isNetworkEnabled: Boolean =
//                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//
//                if (isGpsEnabled) {
//                    emitSignal("_on_location_status_change", "on")
//                } else if (isNetworkEnabled) {
//                    emitSignal("_on_location_status_change", "on")
//                } else {
//                    emitSignal("_on_location_status_change", "off")
//                }
//            }
//        }
//    }
//
//    // Device connect call back
//    private val btleGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
//        // Called when a devices connects or disconnects
//        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//                when (newState) {
//                    BluetoothProfile.STATE_DISCONNECTED -> {
//                        emitSignal("_on_connection_status_change", "disconnected")
//                        connected = false
//                    }
//
//                    BluetoothProfile.STATE_CONNECTED -> {
//                        connected = true
//                        // Read services and characteristics
//                        listServicesAndCharacteristics()
//
//                        emitSignal("_on_connection_status_change", "connected")
//                    }
//                }
//            } else { // There was an issue connecting
//
//                sendDebugSignal(status.toString())
//            }
//        }
//
//        // Called after a BluetoothGatt.discoverServices() call
//        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
//            sendServicesAndCharacteristics(bluetoothGatt.getServices())
//        }
//
//        // Result of a characteristic read operation
//        override fun onCharacteristicRead(
//            gatt: BluetoothGatt?,
//            characteristic: BluetoothGattCharacteristic?,  // byte[] value, For Android Tiramisu we need this
//            status: Int
//        ) {
//            sendDebugSignal("onCharacteristicRead")
//
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//                sendDebugSignal("onCharacteristicRead: SUCCESS")
//            } else {
//                sendDebugSignal("onCharacteristicRead: " + status.toString())
//            }
//        }
//
//        // Result of a characteristic read operation
//        override fun onCharacteristicWrite(
//            gatt: BluetoothGatt?,
//            characteristic: BluetoothGattCharacteristic?,
//            status: Int
//        ) {
//            sendDebugSignal("onCharacteristicWrite")
//
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//                // broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
//                sendDebugSignal("onCharacteristicWrite: SUCCESS")
//            }
//        }
//
//        // Result of a characteristic read/write operation
//        override fun onCharacteristicChanged(
//            gatt: BluetoothGatt?,
//            characteristic: BluetoothGattCharacteristic // ,byte[] value, For Android Tiramisu we need this
//        ) {
//            val data: org.godotengine.godot.Dictionary = Dictionary()
//
//            val characteristic_uuid = characteristic.getUuid().toString()
//            val service_uuid = characteristic.getService().getUuid().toString()
//            val bytes = characteristic.getValue()
//
//            sendDebugSignal("onCharacteristicChanged " + characteristic_uuid)
//
//            data.put("service_uuid", service_uuid)
//            data.put("characteristic_uuid", characteristic_uuid)
//            data.put("bytes", bytes)
//
//            emitSignal("_on_characteristic_read", data)
//        }
//    }
//
//    fun connect(address: String?) {
//        if (!connected) {
//            sendDebugSignal("Connecting to device with address " + address)
//            stopScan()
//            if (hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
//                sendDebugSignal("Cannot connect because you do not have Manifest.permission.BLUETOOTH_CONNECT")
//
//                return
//            }
//            bluetoothGatt =
//                devices.get(address).getDevice().connectGatt(context, false, btleGattCallback)
//        }
//    }
//
//    fun disconnect() {
//        if (connected) {
//            sendDebugSignal("Disconnecting device")
//            if (hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
//                sendDebugSignal("Cannot disconnect because you do not have Manifest.permission.BLUETOOTH_CONNECT")
//
//                return
//            }
//            bluetoothGatt.disconnect()
//        }
//    }
//
//    private fun sendServicesAndCharacteristics(gattServices: MutableList<BluetoothGattService>?) {
//        if (gattServices == null) return
//
//        emitSignal("_on_characteristic_finding", "processing")
//
//        // Loops through available GATT Services.
//        for (gattService in gattServices) {
//            val serviceUuid = gattService.getUuid().toString()
//
//            val gattCharacteristics = gattService.getCharacteristics()
//
//            var characteristicData: org.godotengine.godot.Dictionary?
//
//            // Loops through available Characteristics.
//            for (gattCharacteristic in gattCharacteristics) {
//                characteristicData = Dictionary()
//
//                val characteristicUuid = gattCharacteristic.getUuid().toString()
//
//                characteristicData.put("service_uuid", serviceUuid)
//                characteristicData.put("characteristic_uuid", characteristicUuid)
//                characteristicData.put("real_mask", gattCharacteristic.getProperties())
//
//                // Set all 3 properties to false
//                characteristicData.put("readable", false)
//                characteristicData.put("writable", false)
//                characteristicData.put("writable_no_response", false)
//
//                if ((gattCharacteristic.getProperties() and BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
//                    characteristicData.put("readable", true)
//                }
//
//                if ((gattCharacteristic.getProperties() and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0) {
//                    characteristicData.put("writable", true)
//                }
//
//                if ((gattCharacteristic.getProperties()
//                            and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0
//                ) {
//                    characteristicData.put("writable_no_response", true)
//                }
//
//                emitSignal("_on_characteristic_found", characteristicData)
//            }
//        }
//
//        emitSignal("_on_characteristic_finding", "done")
//    }
//
//    // Read from characteristic
//    private fun readFromCharacteristic(serviceUUID: String?, characteristicUUID: String?) {
//        if (connected) {
//            val service = UUID.fromString(serviceUUID)
//            val characteristic = UUID.fromString(characteristicUUID)
//
//            val gattService: BluetoothGattService? = bluetoothGatt.getService(service)
//            if (gattService == null) {
//                sendDebugSignal("Service not found: " + serviceUUID)
//                return
//            }
//
//            val gattCharacteristic = gattService.getCharacteristic(characteristic)
//            if (gattCharacteristic == null) {
//                sendDebugSignal("Characteristic not found: " + characteristicUUID)
//                return
//            }
//
//            if (hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
//                sendDebugSignal(
//                    "Cannot read characteristics because you do not have Manifest.permission.BLUETOOTH_CONNECT"
//                )
//
//                return
//            }
//
//            val success: Boolean = bluetoothGatt.readCharacteristic(gattCharacteristic)
//            if (!success) {
//                sendDebugSignal("Failed to read from characteristic: " + characteristicUUID)
//            }
//        }
//    }
//
//    // Write bytes to characteristic, automatically detects the write type
//    private fun writeBytesToCharacteristic(
//        serviceUUID: String?,
//        characteristicUUID: String?,
//        data: ByteArray?
//    ) {
//        if (connected) {
//            val service = UUID.fromString(serviceUUID)
//            val characteristic = UUID.fromString(characteristicUUID)
//
//            val c: BluetoothGattCharacteristic =
//                bluetoothGatt.getService(service).getCharacteristic(characteristic)
//            c.setValue(data)
//
//            if (c.getWriteType() == BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT) {
//                c.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
//            } else if (c.getWriteType() == BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE) {
//                c.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
//            }
//
//            if (hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
//                sendDebugSignal(
//                    "Cannot write characteristic because you do not have Manifest.permission.BLUETOOTH_CONNECT"
//                )
//
//                return
//            }
//            bluetoothGatt.writeCharacteristic(c)
//        }
//    }
//
//    // Write bytes to characteristic, automatically detects the write type
//    private fun writeStringToCharacteristic(
//        serviceUUID: String?,
//        characteristicUUID: String?,
//        data: String?
//    ) {
//        if (connected) {
//            val service = UUID.fromString(serviceUUID)
//            val characteristic = UUID.fromString(characteristicUUID)
//
//            val c: BluetoothGattCharacteristic =
//                bluetoothGatt.getService(service).getCharacteristic(characteristic)
//            c.setValue(data)
//
//            if (c.getWriteType() == BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT) {
//                c.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
//            } else if (c.getWriteType() == BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE) {
//                c.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
//            }
//
//            if (hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
//                sendDebugSignal(
//                    "Cannot write characteristic because you do not have Manifest.permission.BLUETOOTH_CONNECT"
//                )
//
//                return
//            }
//            bluetoothGatt.writeCharacteristic(c)
//        }
//    }
//
//    // Subscribe to characteristic
//    private fun subscribeToCharacteristic(serviceUUID: String?, characteristicUUID: String?) {
//        if (connected) {
//            val service = UUID.fromString(serviceUUID)
//            val characteristic = UUID.fromString(characteristicUUID)
//
//            val c: BluetoothGattCharacteristic =
//                bluetoothGatt.getService(service).getCharacteristic(characteristic)
//            if (hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
//                sendDebugSignal(
//                    "Cannot subscribe to characteristic because you do not have Manifest.permission.BLUETOOTH_CONNECT"
//                )
//
//                return
//            }
//            bluetoothGatt.setCharacteristicNotification(c, true)
//
//            // Set the Client Characteristic Config Descriptor to allow server initiated
//            // updates
//            val CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
//            val desc = c.getDescriptor(CONFIG_DESCRIPTOR)
//            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
//            bluetoothGatt.writeDescriptor(desc)
//        }
//    }
//
//    private fun unsubscribeFromCharacteristic(serviceUUID: String?, characteristicUUID: String?) {
//        if (connected) {
//            val service = UUID.fromString(serviceUUID)
//            val characteristic = UUID.fromString(characteristicUUID)
//
//            // Set the Client Characteristic Config Descriptor to disable server initiated
//            // updates
//            val CONFIG_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
//            val c: BluetoothGattCharacteristic =
//                bluetoothGatt.getService(service).getCharacteristic(characteristic)
//
//            val desc = c.getDescriptor(CONFIG_DESCRIPTOR)
//            desc.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
//
//            if (hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
//                sendDebugSignal(
//                    "Cannot unsubscribe from characteristic because you do not have Manifest.permission.BLUETOOTH_CONNECT"
//                )
//
//                return
//            }
//
//            bluetoothGatt.writeDescriptor(desc)
//        }
//    }
//}