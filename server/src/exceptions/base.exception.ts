/* eslint-disable max-classes-per-file */
import { HttpException, HttpStatus } from '@nestjs/common';

enum ErrorCode {
  UserConflict = 3001,
  BadRequest = 2000,
  BadVideoFormat = 8000,
  BadThumbnailFormat = 8100,
}

const ErrorMessage = {
  [ErrorCode.UserConflict]: '이미 가입된 회원입니다',
  [ErrorCode.BadRequest]: '잘못된 요청 형식입니다',
};

export class BaseException extends HttpException {
  constructor(
    public errorCode: number,
    statusCode: HttpStatus,
  ) {
    super(ErrorMessage[errorCode], statusCode);
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
