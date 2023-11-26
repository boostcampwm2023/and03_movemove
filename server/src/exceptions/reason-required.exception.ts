import { HttpStatus } from '@nestjs/common';
import { ErrorCode } from 'src/exceptions/enum/exception.enum';
import { BaseException } from './base.exception';

export class ReasonRequiredException extends BaseException {
  constructor() {
    super(ErrorCode.ReasonRequired, HttpStatus.BAD_REQUEST);
  }
}
