import { IsNumber, IsOptional } from 'class-validator';

export class ManifestQueryDto {
  /**
   * Seed 값 (Optional)
   */
  @IsOptional()
  @IsNumber()
  seed: number;
}
