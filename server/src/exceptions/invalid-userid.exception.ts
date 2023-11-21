import { HttpStatus } from '@nestjs/common';
import { ErrorCodeEnum } from 'src/enum/exception.enum';
import { BaseException } from './base.exception';

export class InvalidUserIDException extends BaseException {
  constructor() {
    super(ErrorCodeEnum.InvalidUserID, HttpStatus.UNAUTHORIZED);
  }
}
