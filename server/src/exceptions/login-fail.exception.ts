import { HttpStatus } from '@nestjs/common';
import { ErrorCodeEnum } from 'src/enum/exception.enum';
import { BaseException } from './base.exception';

export class LoginFailException extends BaseException {
  constructor() {
    super(ErrorCodeEnum.UserConflict, HttpStatus.UNAUTHORIZED);
  }
}
