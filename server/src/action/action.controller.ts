import { Controller, Param, Patch, Query, UseGuards } from '@nestjs/common';
import { ApiFailResponse } from 'src/decorators/api-fail-response';
import { VideoNotFoundException } from 'src/exceptions/video-not-found.exception';
import { RequestUser, User } from 'src/decorators/request-user';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { AuthGuard } from 'src/auth/auth.guard';
import { InvalidTokenException } from 'src/exceptions/invalid-token.exception';
import { TokenExpiredException } from 'src/exceptions/token-expired.exception';
import { SeedQueryDto } from './dto/manifest-query.dto';
import { ActionService } from './action.service';

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
  @Patch('/:videoId/views')
  @ApiFailResponse('비디오를 찾을 수 없음', [VideoNotFoundException])
  viewVideo(
    @Param('videoId') videoId: string,
    @RequestUser() user: User,
    @Query() query: SeedQueryDto,
  ) {
    return this.actionService.viewVideo(videoId, user.id, query.seed);
  }
}
