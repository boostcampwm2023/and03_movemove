import { HttpStatus } from '@nestjs/common';
import { ErrorCodeEnum } from 'src/enum/exception.enum';
import { BaseException } from './base.exception';

export class TokenExpiredException extends BaseException {
  constructor() {
    super(ErrorCodeEnum.TokenExpired, HttpStatus.UNAUTHORIZED);
  }
}
