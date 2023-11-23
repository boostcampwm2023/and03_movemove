import { HttpStatus } from '@nestjs/common';
import { ErrorCode } from 'src/exceptions/enum/exception.enum';
import { BaseException } from './base.exception';

export class UserNotFoundException extends BaseException {
  constructor() {
    super(ErrorCode.UserNotFound, HttpStatus.NOT_FOUND);
  }
}
