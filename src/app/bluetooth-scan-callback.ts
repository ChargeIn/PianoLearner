const {ScanCallback} = android.bluetooth.le;

export class BluetoothScanCallback extends ScanCallback {
  public onBatchScanResults(result: java.util.List<android.bluetooth.le.ScanResult>) {
  }

  public onScanFailed(failType: number) {
  };

  public onScanResult(resultType: number, result: android.bluetooth.le.ScanResult) {
    console.log(result);
  };
}
