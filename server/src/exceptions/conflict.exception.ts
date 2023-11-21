import { HttpStatus } from '@nestjs/common';
import { BaseException } from './base.exception';
import { ErrorCodeEnum } from 'src/enum/exception.enum';

export class UserConflictException extends BaseException {
  constructor() {
    super(ErrorCodeEnum.UserConflict, HttpStatus.CONFLICT);
  }
}
