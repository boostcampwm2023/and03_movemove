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

  @Get('test')
  test() {
    return requestEncoding(process.env.INPUT_BUCKET, ['lplbisang.mp4'])
      .then((response) => {
        return response.data;
      })
      .catch((error) => {
        if (error.response) {
          return error.response.data;
        }
        return error;
      });
  }

  @Get('test/putObject')
  testPutObject() {
    return putObject(
      'video-thimbnail-bucket',
      'sample-object2.txt',
      'hellohello2',
    )
      .then((response) => {
        return `put success: ${response.data}`;
      })
      .catch((error) => {
        if (error.response) {
          return error.response.data;
        }
        return error;
      });
  }

  @Get('test/getObject')
  @UseGuards(AuthGuard)
  testGetObject() {
    return getObject('video-thimbnail-bucket', 'sample-object2.txt')
      .then((response) => {
        return response.data;
      })
      .catch((error) => {
        if (error.response) {
          return error.response.data;
        }
        return error;
      });
  }

  @Get('test/deleteObject')
  testDeleteObject() {
    return deleteObject('video-thimbnail-bucket', 'sample-object2.txt')
      .then((response) => {
        return `delete success:${response.data}`;
      })
      .catch((error) => {
        if (error.response) {
          return error.response.data;
        }
        return error;
      });
  }
}
