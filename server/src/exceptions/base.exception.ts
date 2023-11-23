/* eslint-disable max-classes-per-file */
import { HttpException, HttpStatus } from '@nestjs/common';
import { ApiProperty } from '@nestjs/swagger';
import { ErrorCode, ErrorMessage } from 'src/exceptions/enum/exception.enum';

export class BaseException extends HttpException {
  errorCode: ErrorCode;

  @ApiProperty()
  statusCode: string;

  @ApiProperty()
  message: string;

  constructor(errorCode: ErrorCode, statusCode: HttpStatus) {
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
