import { Controller, Get } from '@nestjs/common';
import { AppService } from './app.service';
import { putObject } from './ncpAPI/putObject';

@Controller()
export class AppController {
  constructor(private appService: AppService) {}

  @Get('ads')
  getAds() {
    return this.appService.getAds();
  }
  @Get('test')
  test() {
    return requestEncoding(process.env.INPUT_BUCKET, ['lplbisang.mp4'])
      .then(function (response) {
        return response.data;
      })
      .catch(function (error) {
        if (error.response) {
          return error.response.data;
        }
        return error;
      });
  }
  @Get('test2')
  test2() {
    return putObject(
      'video-thimbnail-bucket',
      'sample-object.txt',
      'hellohello',
    )
      .then(function (response) {
        return response.data;
      })
      .catch(function (error) {
        if (error.response) {
          return error.response.data;
        }
        return error;
      });
  }
}
