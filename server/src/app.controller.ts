import { Controller, Get } from '@nestjs/common';
import { AppService } from './app.service';
import { requestEncoding } from './ncpAPI/requestEncoding';
import { putObject } from './ncpAPI/putObject';
import { getObject } from './ncpAPI/getObject';

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
      'sample-object2.txt',
      'hellohello2',
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
  @Get('test/getObject')
  testGetObject() {
    return getObject('video-thimbnail-bucket', 'sample-object2.txt')
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
