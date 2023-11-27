import { IsInt, IsPositive } from 'class-validator';

export class UserRatedVideoQueryDto {
  /**
   * 요청하는 비디오 수
   */
  @IsInt()
  @IsPositive()
  limit: number;

  /**
   * 마지막으로 조회한 비디오의 ratedAt
   */
  lastRatedAt?: string;
}
