import {Injectable} from "@angular/core";

@Injectable({ providedIn: 'root'})
export class LoggerService {
  error(msg: string) {
    console.error(msg);
  }

  log(msg: string) {
    console.log(msg);
  }
}
