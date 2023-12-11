import { HttpStatus } from '@nestjs/common';
import { ErrorCode } from 'src/exceptions/enum/exception.enum';
import { BaseException } from './base.exception';

export class GreenEyeActionFailException extends BaseException {
  constructor() {
    super(ErrorCode.GreenEyeActionFail, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
