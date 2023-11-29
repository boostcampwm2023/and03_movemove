import { ApiProperty } from '@nestjs/swagger';

export class AdsResponseDto {
  @ApiProperty({ type: [String] })
  adImages: [string];
}
