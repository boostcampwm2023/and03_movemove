import { HttpStatus } from '@nestjs/common';
import { ErrorCode } from 'src/exceptions/enum/exception.enum';
import { BaseException } from './base.exception';

export class InvalidGoogldIdTokenException extends BaseException {
  constructor() {
    super(ErrorCode.InvalidGoogleIdToken, HttpStatus.UNAUTHORIZED);
  }
}
