import { ApiProperty } from '@nestjs/swagger';

export class UserDto {
  @ApiProperty({
    description: 'UUID',
    format: 'uuid',
    example: '550e8400-e29b-41d4-a716-446655440000',
  })
  uuid: string;
  @ApiProperty({
    description: '유저 프로필 사진',
    type: 'string',
    format: 'binary',
  })
  profileImage: Express.Multer.File;
  @ApiProperty({ description: '유저 닉네임', example: '페이커' })
  nickname: string;
  @ApiProperty({
    description: '한줄 소개',
    example: '모든 길은 저를 통할 것입니다',
  })
  statusMessage: string;
}
