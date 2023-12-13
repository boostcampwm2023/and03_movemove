import { Controller, Param, Put, Query, UseGuards } from '@nestjs/common';
import { ApiFailResponse } from 'src/decorators/api-fail-response';
import { VideoNotFoundException } from 'src/exceptions/video-not-found.exception';
import { RequestUser, User } from 'src/decorators/request-user';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { AuthGuard } from 'src/auth/auth.guard';
import { InvalidTokenException } from 'src/exceptions/invalid-token.exception';
import { TokenExpiredException } from 'src/exceptions/token-expired.exception';
import { ApiSuccessResponse } from 'src/decorators/api-succes-response';
import { SeedQueryDto } from './dto/manifest-query.dto';
import { ActionService } from './action.service';
import { ViewResponseDto } from './dto/view-response.dto';

@ApiTags('VIDEO')
@ApiBearerAuth()
@UseGuards(AuthGuard)
@ApiFailResponse('인증 실패', [InvalidTokenException, TokenExpiredException])
@Controller('videos')
export class ActionController {
  constructor(private actionService: ActionService) {}

  /**
   * 조회수 증가 (비디오 시청)
   */
  @Put('/:videoId/views')
  @ApiSuccessResponse(200, '비디오 조회수 증가', ViewResponseDto)
  @ApiFailResponse('비디오를 찾을 수 없음', [VideoNotFoundException])
  viewVideo(
    @Param('videoId') videoId: string,
    @RequestUser() user: User,
    @Query() query: SeedQueryDto,
  ) {
    console.log(`조회수 증가 API : ${JSON.stringify(query)}`);
    return this.actionService.viewVideo(videoId, user.id, query.seed);
  }
}
