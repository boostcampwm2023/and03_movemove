/* eslint-disable max-classes-per-file */
import { HttpException, HttpStatus } from '@nestjs/common';
import { ErrorCodeEnum, ErrorMessage } from 'src/enum/exception.enum';

export class BaseException extends HttpException {
  errorCode: ErrorCodeEnum;

  constructor(errorCode: ErrorCodeEnum, statusCode: HttpStatus) {
    super(ErrorMessage[errorCode], statusCode);
    this.errorCode = errorCode;
  }
}

export class VideoFormatException extends BaseException {
  constructor() {
    super(ErrorCode.BadVideoFormat, HttpStatus.BAD_REQUEST);
  }
}

export class ThumbnailFormatException extends BaseException {
  constructor() {
    super(ErrorCode.BadThumbnailFormat, HttpStatus.BAD_REQUEST);
  }
}
