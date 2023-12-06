import { HttpStatus } from '@nestjs/common';
import { ErrorCode } from 'src/exceptions/enum/exception.enum';
import { BaseException } from './base.exception';

export class EncodingActionFailException extends BaseException {
  constructor() {
    super(ErrorCode.EncodingActionFail, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
