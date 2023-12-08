import { HttpStatus } from '@nestjs/common';
import { ErrorCode } from 'src/exceptions/enum/exception.enum';
import { BaseException } from './base.exception';

export class GreenEyeApiFailException extends BaseException {
  constructor() {
    super(ErrorCode.GreenEyeApiFail, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
