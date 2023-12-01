import { IsNotEmpty, IsUUID } from 'class-validator';
import { ProfilePresignedUrlRequestDto } from './profile-presigned-url-request.dto';

export class SignupProfilePresignedUrlRequestDto extends ProfilePresignedUrlRequestDto {
  /**
   * 유저 ID
   * @example '550e8400-e29b-41d4-a716-446655440000'
   */
  @IsUUID()
  uuid: string;

  /**
   * 소셜 accessToken
   * @example '1/fFAGRNJru1FTz70BzhT3Zg'
   */
  @IsNotEmpty()
  accessToken: string;
}
