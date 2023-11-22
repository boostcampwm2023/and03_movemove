import { HttpStatus } from '@nestjs/common';
import { ErrorCodeEnum } from 'src/enum/exception.enum';
import { BaseException } from './base.exception';

export class InvalidTokenException extends BaseException {
  constructor() {
    super(ErrorCodeEnum.InvalidToken, HttpStatus.UNAUTHORIZED);
  }
}
