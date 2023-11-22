import { HttpStatus } from '@nestjs/common';
import { ErrorCodeEnum } from 'src/enum/exception.enum';
import { BaseException } from './base.exception';

export class OAuthFailedException extends BaseException {
  constructor() {
    super(ErrorCodeEnum.OAuthFailed, HttpStatus.UNAUTHORIZED);
  }
}
