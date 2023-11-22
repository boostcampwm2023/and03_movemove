import { ExceptionFilter, Catch, ArgumentsHost } from '@nestjs/common';
import { BaseException } from 'src/exceptions/base.exception';
import { Response } from 'express';

@Catch(BaseException)
export class BaseExceptionFilter implements ExceptionFilter {
  catch(exception: BaseException, host: ArgumentsHost) {
    const ctx = host.switchToHttp();
    const response = ctx.getResponse<Response>();
    const status = exception.getStatus();

    response.status(status).json({
      statusCode: status,
      errorCode: exception.errorCode,
      message: exception.message,
    });
  }
}
