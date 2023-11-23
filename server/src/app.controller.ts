import { Controller, Get, UseGuards } from '@nestjs/common';
import { AppService } from './app.service';
import { requestEncoding } from './ncpAPI/requestEncoding';
import { putObject } from './ncpAPI/putObject';
import { getObject } from './ncpAPI/getObject';
import { deleteObject } from './ncpAPI/deleteObject';
import { AuthGuard } from './auth/auth.guard';

@Controller()
export class AppController {
  constructor(private appService: AppService) {}

  @Get('ads')
  getAds() {
    return this.appService.getAds();
  }
}
