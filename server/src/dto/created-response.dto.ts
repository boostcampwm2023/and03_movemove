import { ApiProperty } from '@nestjs/swagger';
import { BaseResponse } from 'transform.interceptor';

export class CreatedResponse<T> implements BaseResponse<T> {
  /**
   * Post 성공
   * @example 201
   */
  statusCode: number;

  /**
   * @example 성공
   */
  message: string;

  @ApiProperty()
  data: T;
}
