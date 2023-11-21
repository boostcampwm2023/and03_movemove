import { HttpStatus } from '@nestjs/common';
import { ErrorCodeEnum } from 'src/enum/exception.enum';
import { BaseException } from './base.exception';

export class BadTokenFormatException extends BaseException {
  constructor() {
    super(ErrorCodeEnum.BadTokenFormat, HttpStatus.UNAUTHORIZED);
  }
}
