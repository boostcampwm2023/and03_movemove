import { HttpStatus } from '@nestjs/common';
import { ErrorCode } from 'src/enum/exception.enum';
import { BaseException } from './base.exception';

export class TokenExpiredException extends BaseException {
  constructor() {
    super(ErrorCode.TokenExpired, HttpStatus.UNAUTHORIZED);
  }
}
