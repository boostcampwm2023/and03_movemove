import {
  Injectable,
  NestInterceptor,
  ExecutionContext,
  CallHandler,
  HttpStatus,
} from '@nestjs/common';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

export interface Response<T> {
  statusCode: number;
  message: string;
  data: T;
}
export const IgnoreSymbol = Symbol('ignore');

@Injectable()
export class TransformInterceptor<T>
  implements NestInterceptor<T, Response<T>>
{
  intercept(
    context: ExecutionContext,
    next: CallHandler,
  ): Observable<Response<T>> {
    const start = Date.now();
    const response = context.getArgByIndex(1);
    const { statusCode } = response;
    const message = HttpStatus[statusCode];

    const isIgnored = context.getHandler()[IgnoreSymbol];

    if (isIgnored) {
      return next.handle();
    }
    return next.handle().pipe(
      tap(() => console.log(`After... ${Date.now() - start}ms`)),
      map((data) => ({ statusCode, message, data })),
    );
  }
}
