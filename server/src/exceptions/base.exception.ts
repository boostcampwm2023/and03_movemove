import { HttpException, HttpStatus } from '@nestjs/common';
import { ApiProperty } from '@nestjs/swagger';
import {
  ErrorCodeEnum,
  ErrorDescription,
  ErrorMessage,
} from 'src/enum/exception.enum';

export class BaseException extends HttpException {
  constructor(errorCode: ErrorCodeEnum, statusCode: HttpStatus) {
    super(ErrorMessage[errorCode], statusCode);
    this.statusCode = errorCode;
    this.message = ErrorMessage[errorCode];
    this.description = ErrorDescription[errorCode];
  }

  @ApiProperty()
  statusCode: number;

  @ApiProperty()
  message: string;

  description: string;
}
