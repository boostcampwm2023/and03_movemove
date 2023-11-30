import { IsNumber, IsOptional } from 'class-validator';

export class SeedQueryDto {
  /**
   * Seed 값
   */
  @IsOptional()
  @IsNumber()
  seed?: number;
}
