import { HttpException, HttpStatus } from '@nestjs/common';
import { ErrorCodeEnum, ErrorMessage } from 'src/enum/exception.enum';

export class BaseException extends HttpException {
  errorCode: ErrorCodeEnum;

  constructor(errorCode: ErrorCodeEnum, statusCode: HttpStatus) {
    super(ErrorMessage[errorCode], statusCode);
    this.errorCode = errorCode;
  }
}
