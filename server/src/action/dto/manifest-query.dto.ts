import { IsNumber, IsOptional } from 'class-validator';

export class ManifestQueryDto {
  /**
   * Seed 값
   */
  @IsOptional()
  @IsNumber()
  seed?: number;
}
