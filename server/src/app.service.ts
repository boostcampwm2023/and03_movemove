import { Injectable } from '@nestjs/common';

@Injectable()
export class AppService {
  getAds() {
    return `get ads`;
  }
}
