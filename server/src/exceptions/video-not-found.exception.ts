import { HttpStatus } from '@nestjs/common';
import { ErrorCode } from 'src/exceptions/enum/exception.enum';
import { BaseException } from './base.exception';

export class VideoNotFoundException extends BaseException {
  constructor() {
    super(ErrorCode.VideoNotFound, HttpStatus.NOT_FOUND);
  }
}
