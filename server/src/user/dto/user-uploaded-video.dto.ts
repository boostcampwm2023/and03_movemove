import { IsInt, IsPositive } from 'class-validator';

export class UserUploadedVideoQueryDto {
  /**
   * 요청하는 비디오 수
   */
  @IsInt()
  @IsPositive()
  limit: number;

  lastId?: string;
}
