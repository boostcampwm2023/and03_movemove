enum ErrorCode {
  UserConflict = 3001,
  VideoConflict = 3002,
  BadRequest = 2000,
  LoginFail = 1000,
  TokenExpired = 1001,
  InvalidToken = 1002,
  BadTokenFormat = 1003,
  OAuthFailed = 1004,
  InvalidRefreshToken = 1005,
  VideoNotFound = 4000,
  UserNotFound = 4001,
  ObjectNotFound = 4002,
  NotYourVideo = 5000,
  NeverViewVideo = 5001,
  ProfileUploadRequired = 5002,
  ThumbnailUploadRequired = 5003,
  VideoUploadRequired = 5004,
  BadVideoFormat = 8000,
  BadThumbnailFormat = 8100,
  BadRequestFormat = 8200,
}

const ErrorMessage = {
  [ErrorCode.UserConflict]: '이미 가입된 회원',
  [ErrorCode.VideoConflict]: '중복된 Video Id',
  [ErrorCode.BadRequest]: '잘못된 요청 형식',
  [ErrorCode.LoginFail]: '가입되지 않은 회원',
  [ErrorCode.TokenExpired]: 'AccessToken 만료',
  [ErrorCode.InvalidToken]: 'AccessToken 검증 오류',
  [ErrorCode.OAuthFailed]: '소셜 Token 인증 오류',
  [ErrorCode.InvalidRefreshToken]: '유효하지 않은 RefreshToken',
  [ErrorCode.BadVideoFormat]: '비디오 포맷 오류',
  [ErrorCode.BadThumbnailFormat]: '썸네일 포맷 오류',
  [ErrorCode.NotYourVideo]: '업로더만이 요청할 수 있음',
  [ErrorCode.NeverViewVideo]: '시청한 영상만 별점을 등록할 수 있음',
  [ErrorCode.VideoNotFound]: '비디오를 찾을 수 없음',
  [ErrorCode.UserNotFound]: '유저를 찾을 수 없음',
  [ErrorCode.ObjectNotFound]: '오브젝트를 찾을 수 없음',
  [ErrorCode.ProfileUploadRequired]: '프로필 이미지를 먼저 업로드 해야합니다.',
  [ErrorCode.ThumbnailUploadRequired]: '썸네일을 먼저 업로드 해야합니다.',
  [ErrorCode.VideoUploadRequired]: '비디오를 먼저 업로드 해야합니다.',
  [ErrorCode.BadRequestFormat]: '요청 형식이 잘못됨',
};

export { ErrorCode, ErrorMessage };
