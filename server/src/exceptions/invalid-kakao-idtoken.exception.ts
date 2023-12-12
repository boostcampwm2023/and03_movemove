import { ErrorCode } from 'src/exceptions/enum/exception.enum';
import { HttpStatus } from '@nestjs/common';
import { BaseException } from './base.exception';

export class InvalidKakaoIdTokenException extends BaseException {
  constructor() {
    super(ErrorCode.InvalidKakaoIdToken, HttpStatus.UNAUTHORIZED);
  }
}
