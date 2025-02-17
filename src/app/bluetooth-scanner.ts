import {Utils} from "@nativescript/core";
import {inject} from "@angular/core";
import {LoggerService} from "~/app/logging/logger.service";
import * as geolocation from '@nativescript/geolocation';
import {BluetoothScanCallback} from "~/app/bluetooth-scan-callback";
const { BLUETOOTH_SERVICE } = android.content.Context;
const { ScanFilter, ScanSettings, ScanCallback } = android.bluetooth.le;
const { ArrayList, UUID } = java.util;

// https://developer.android.com/reference/android/media/midi/package-summary#btle_scan_devices
const BLE_MIDI_SERVICE_UUID = "03B80E5A-EDE8-4B33-A751-6CE34EC4C700"


export class BluetoothScanner {
  logger = inject(LoggerService);

  bluetoothLeScanner: android.bluetooth.le.BluetoothLeScanner;
  devices = [];

  scanning = false;

  constructor() {
    const appContext = Utils.android.getApplicationContext();
    const bluetoothManager: android.bluetooth.BluetoothManager = appContext.getSystemService(BLUETOOTH_SERVICE);
    this.bluetoothLeScanner = bluetoothManager.getAdapter().getBluetoothLeScanner();
  }

  scanForDevices() {
    if(!this.scanning) {
      return;
    }

    this.logger.log("Getting permissions required for bluetooth…");

    geolocation.enableLocationRequest().then(() => {
      this.logger.log("Successfully retrieved permissions.");

      this.logger.log("Getting BLE MIDI devices…");
      this.startScan();

    }).catch(() => {
      this.logger.error("Could not retrieve geo location permissions.");
    });
  }

  private startScan() {
    this.logger.log("Start a bluetooth scan…");

    this.scanning = true;
    this.devices = [];

    let serviceUuid = UUID.fromString(BLE_MIDI_SERVICE_UUID);

    const filter = new ScanFilter.Builder()
      .setServiceUuid(new android.os.ParcelUuid(serviceUuid));

    const scanFilters = new ArrayList();
    scanFilters.add(filter)

    const scanSettings = new ScanSettings.Builder()
      .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
      .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
      .build();

    const callback = new BluetoothScanCallback();

    this.bluetoothLeScanner.startScan(scanFilters, scanSettings, callback);
  }

  private onScanSuccess(callbackType: number, result) {
    if(callbackType !== ScanSettings.CALLBACK_TYPE_ALL_MATCHES) {
      // should not happen
      return;
    }

    this.scanning = false;
  }

  private onScanFailed(errorCode: number) {
    this.scanning = false;

    if(errorCode === ScanCallback.SCAN_FAILED_ALREADY_STARTED) {
      this.logger.error("Bluetooth Scan Failed: Scan already started.");
      return;
    }

    if(errorCode === ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED) {
      this.logger.error("Bluetooth Scan Failed: Could not register application.");
      return;
    }

    if(errorCode === ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED) {
      this.logger.error("Bluetooth Scan Failed: Not Supported.")
      return;
    }

    if(errorCode === ScanCallback.SCAN_FAILED_INTERNAL_ERROR) {
      this.logger.error("Bluetooth Scan failed: Internal Server Error.")
      return;
    }
  }
}
