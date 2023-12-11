import { HttpStatus } from '@nestjs/common';
import { ErrorCode } from 'src/exceptions/enum/exception.enum';
import { BaseException } from './base.exception';

export class InconsistentKakaoUuidException extends BaseException {
  constructor() {
    super(ErrorCode.InconsistentKakaoUuid, HttpStatus.UNAUTHORIZED);
  }
}
