import { HttpStatus } from '@nestjs/common';
import { ErrorCode } from 'src/exceptions/enum/exception.enum';
import { BaseException } from './base.exception';

export class NeverViewVideoException extends BaseException {
  constructor() {
    super(ErrorCode.NeverViewVideo, HttpStatus.FORBIDDEN);
  }
}
