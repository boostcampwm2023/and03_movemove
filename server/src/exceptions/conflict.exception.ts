import { HttpStatus } from '@nestjs/common';
import { ErrorCode } from 'src/enum/exception.enum';
import { BaseException } from './base.exception';

export class UserConflictException extends BaseException {
  constructor() {
    super(ErrorCode.UserConflict, HttpStatus.CONFLICT);
  }
}
