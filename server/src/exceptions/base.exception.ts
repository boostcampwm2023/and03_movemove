import { HttpException, HttpStatus } from '@nestjs/common';
import { ApiProperty } from '@nestjs/swagger';
import { ErrorCodeEnum, ErrorMessage } from 'src/enum/exception.enum';

export class BaseException extends HttpException {
  constructor(errorCode: ErrorCodeEnum, statusCode: HttpStatus) {
    super(ErrorMessage[errorCode], statusCode);
    this.errorCode = errorCode;
    this.message = ErrorMessage[errorCode];
  }
  @ApiProperty()
  errorCode: number;

  @ApiProperty()
  message: string;
}
