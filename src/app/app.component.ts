import {Component, inject, NO_ERRORS_SCHEMA} from '@angular/core';
import { PageRouterOutlet } from '@nativescript/angular';
import {BluetoothScanner} from "~/app/bluetooth-scanner";
import {LoggerService} from "~/app/logging/logger.service";

@Component({
  selector: 'ns-app',
  templateUrl: './app.component.html',
  imports: [PageRouterOutlet],
  schemas: [NO_ERRORS_SCHEMA],
})
export class AppComponent {
}
