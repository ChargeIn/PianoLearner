@NativeClass
export class BluetoothScanCallback extends android.bluetooth.le.ScanCallback {
  onBatchScanResults(param0: java.util.List<android.bluetooth.le.ScanResult>): void {
    console.log("batch result", param0)
  }

  onScanFailed(param0: number): void {
    console.log("scan failed", param0)
  }

  onScanResult(param0: number, param1: android.bluetooth.le.ScanResult): void {
    console.log("scan result", param0)
  }
}
