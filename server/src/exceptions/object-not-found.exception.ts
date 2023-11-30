import { HttpStatus } from '@nestjs/common';
import { ErrorCode } from 'src/exceptions/enum/exception.enum';
import { BaseException } from './base.exception';

export class ObjectNotFoundException extends BaseException {
  constructor() {
    super(ErrorCode.ObjectNotFound, HttpStatus.NOT_FOUND);
  }
}
