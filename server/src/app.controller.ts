import { Controller, Get } from '@nestjs/common';
import { ApiTags } from '@nestjs/swagger';
import { AppService } from './app.service';

@Controller()
export class AppController {
  constructor(private appService: AppService) {}

  /**
   * 광고 이미지 응답
   */
  @ApiTags('COMPLETE')
  @Get('ads')
  getAds() {
    return this.appService.getAds();
  }
}
