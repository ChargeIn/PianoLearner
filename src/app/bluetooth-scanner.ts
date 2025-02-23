import {
  AndroidActivityRequestPermissionsEventData,
  AndroidActivityResultEventData,
  Application,
  Utils
} from "@nativescript/core";
import {LoggerService} from "~/app/logging/logger.service";
import * as geolocation from '@nativescript/geolocation';
import {BluetoothScanCallback} from "~/app/bluetooth-scan-callback";

const {BLUETOOTH_SERVICE} = android.content.Context;
const {ScanFilter, ScanSettings, ScanCallback} = android.bluetooth.le;
const {ArrayList, UUID} = java.util;

// https://developer.android.com/reference/android/media/midi/package-summary#btle_scan_devices
const BLE_MIDI_SERVICE_UUID = "03B80E5A-EDE8-4B33-A751-6CE34EC4C700";
const ACTION_REQUEST_ENABLE_BLUETOOTH_REQUEST_CODE = 223;
const PERMISSIONS_REQUEST_CODE = 222;

export class BluetoothScanner {
  bluetoothLeScanner: android.bluetooth.le.BluetoothLeScanner;
  bluetoothAdapter: android.bluetooth.BluetoothAdapter;
  devices = [];

  scanning = false;

  constructor(private logger: LoggerService) {
    const appContext = Utils.android.getApplicationContext();
    const bluetoothManager: android.bluetooth.BluetoothManager = appContext.getSystemService(BLUETOOTH_SERVICE);
    this.bluetoothAdapter = bluetoothManager.getAdapter();

    if (!this.bluetoothAdapter) {
      // device does not support bluetooth;
      return;
    }

    this.bluetoothLeScanner = this.bluetoothAdapter.getBluetoothLeScanner();
  }

  scanForDevices() {
    if (this.scanning || !this.bluetoothAdapter) {
      return new Promise(resolve => resolve(false));
    }

    return this.checkGeoLocationPermissions()
      .then(result => {
        if (result) {
          return this.requestPermissions();
        }
        return false;
      })
      .then(result => {
        if (result) {
          return this.enableBluetooth();
        }
        return false;
      }).then((result) => {
        if (result) {
          this.startScan();
        }
      }).catch(e => {
        this.logger.error("Bluetooth Scan failed: " + e)
      })
  }

  private checkGeoLocationPermissions() {
    this.logger.log("Getting permissions for geolocation…");

    return new Promise<boolean>(resolve => {
      geolocation.enableLocationRequest().then(() => {
        this.logger.log("Successfully retrieved geo location permissions.");

        resolve(true);
      }).catch((e) => {
        this.logger.error("Could not retrieve geo location permissions: " + e);
        resolve(false);
      });
    })
  }

  private startScan() {
    this.logger.log("Start a bluetooth scan…");

    this.scanning = true;
    this.devices = [];

    let serviceUuid = UUID.fromString(BLE_MIDI_SERVICE_UUID);

    try {
      const filter = new ScanFilter.Builder()
        .setServiceUuid(new android.os.ParcelUuid(serviceUuid))
        .build();

      const scanFilters = new ArrayList();
      scanFilters.add(filter)

      const scanSettings = new ScanSettings.Builder()
        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build();

      const callback = new BluetoothScanCallback();
      callback.onScanFailed = this.onScanFailed.bind(this);
      callback.onBatchScanResults = this.onScanSuccess.bind(this);

      this.bluetoothLeScanner.startScan(scanFilters, scanSettings, callback);

    } catch (e) {
      this.logger.error(e);
    }
  }

  private onScanSuccess(callbackType: number, result) {
    console.log("sucess", callbackType, result)
    this.scanning = false;
    if (callbackType !== ScanSettings.CALLBACK_TYPE_ALL_MATCHES) {
      // should not happen
      return;
    }
  }

  private onScanFailed(errorCode: number) {
    console.log("errror", errorCode)
    this.scanning = false;

    if (errorCode === ScanCallback.SCAN_FAILED_ALREADY_STARTED) {
      this.logger.error("Bluetooth Scan Failed: Scan already started.");
      return;
    }

    if (errorCode === ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED) {
      this.logger.error("Bluetooth Scan Failed: Could not register application.");
      return;
    }

    if (errorCode === ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED) {
      this.logger.error("Bluetooth Scan Failed: Not Supported.")
      return;
    }

    if (errorCode === ScanCallback.SCAN_FAILED_INTERNAL_ERROR) {
      this.logger.error("Bluetooth Scan failed: Internal Server Error.")
      return;
    }
  }

  public requestPermissions() {
    this.logger.log("Start requesting permissions…");

    return new Promise<boolean>(resolve => {
      const onRequestPermissionResult = (args: AndroidActivityRequestPermissionsEventData) => {
        if (args.requestCode !== PERMISSIONS_REQUEST_CODE) {
          return;
        }
        Application.android.off(Application.android.activityRequestPermissionsEvent, onRequestPermissionResult);

        let permissionsGranted = true;

        for (let i = 0; i < args.grantResults.length; i++) {
          const result = args.grantResults[i];
          console.log(args.permissions[i], result)
          // RESULT_OK = 0
          if (result !== 0) {
            this.logger.log("Could not receive all permissions. Missing permission: " + args.permissions[i]);
            permissionsGranted = false;
          }
        }

        resolve(permissionsGranted);
      }

      Application.android.on(Application.android.activityRequestPermissionsEvent, onRequestPermissionResult);

      const activity = Application.android.foregroundActivity || Application.android.startActivity;
      activity.requestPermissions(
        [
          'android.permission.BLUETOOTH_CONNECT',
          'android.permission.BLUETOOTH_SCAN',
          'android.permission.ACCESS_FINE_LOCATION',
          'android.permission.ACCESS_COARSE_LOCATION'
        ],
        PERMISSIONS_REQUEST_CODE
      )
    })
  }

  public enableBluetooth() {
    this.logger.log("Start enabling bluetooth…")

    return new Promise((resolve, reject) => {
      if (this.bluetoothAdapter.isEnabled()) {
        this.logger.log("Bluetooth already enabled.")
        return resolve(true);
      }
      try {
        this.logger.log("Asking to enable bluetooth…");
        // activityResult event
        const onBluetoothEnableResult = (args: AndroidActivityResultEventData) => {
          this.logger.log(`Bluetooth enable result: RequestCode ${args.requestCode}, result: ${args.resultCode}`)

          if (args.requestCode === ACTION_REQUEST_ENABLE_BLUETOOTH_REQUEST_CODE) {
            try {
              // remove the event listener
              Application.android.off(Application.android.activityResultEvent, onBluetoothEnableResult);

              // RESULT_OK = -1
              if (args.resultCode === android.app.Activity.RESULT_OK) {
                resolve(true);
              } else {
                resolve(false);
              }
            } catch (ex) {
              this.logger.error("Could not enable bluetooth: " + ex);
              Application.android.off(Application.android.activityResultEvent, onBluetoothEnableResult);
              reject(ex);
              return;
            }
          } else {
            Application.android.off(Application.android.activityResultEvent, onBluetoothEnableResult);
            resolve(false);
            return;
          }
        };

        // set the onBluetoothEnableResult for the intent
        Application.android.on(Application.android.activityResultEvent, onBluetoothEnableResult);

        // create the intent to start the bluetooth enable request
        const intent = new android.content.Intent(android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE);
        const activity = Application.android.foregroundActivity || Application.android.startActivity;

        this.logger.log("Start bluetooth intend…")

        activity.startActivityForResult(intent, ACTION_REQUEST_ENABLE_BLUETOOTH_REQUEST_CODE);
      } catch (ex) {
        this.logger.error("Could not enable bluetooth: " + ex);
        reject(ex);
      }
    });
  }
}
