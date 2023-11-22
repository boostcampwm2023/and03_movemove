import { HttpStatus } from '@nestjs/common';
import { ErrorCode } from 'src/enum/exception.enum';
import { BaseException } from './base.exception';

export class OAuthFailedException extends BaseException {
  constructor() {
    super(ErrorCode.OAuthFailed, HttpStatus.UNAUTHORIZED);
  }
}
