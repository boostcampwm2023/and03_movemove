import { HttpException, HttpStatus } from '@nestjs/common';
import { ApiProperty } from '@nestjs/swagger';
import { ErrorCodeEnum, ErrorMessage } from 'src/enum/exception.enum';

export class BaseException extends HttpException {
  constructor(errorCode: ErrorCodeEnum, statusCode: HttpStatus) {
    super(ErrorMessage[errorCode], statusCode);
    this.statusCode = errorCode;
    this.message = ErrorMessage[errorCode];
  }

  @ApiProperty()
  statusCode: number;

  @ApiProperty()
  message: string;
}
