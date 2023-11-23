enum ErrorCode {
  UserConflict = 3001,
  BadRequest = 2000,
  LoginFail = 1000,
  TokenExpired = 1001,
  InvalidToken = 1002,
  BadTokenFormat = 1003,
  OAuthFailed = 1004,
  InvalidRefreshToken = 1005,
  VideoNotFound = 4000,
  NotYourVideo = 5000,
  BadVideoFormat = 8000,
  BadThumbnailFormat = 8100,
}

const ErrorMessage = {
  [ErrorCode.UserConflict]: '이미 가입된 회원',
  [ErrorCode.BadRequest]: '잘못된 요청 형식',
  [ErrorCode.LoginFail]: '가입되지 않은 회원',
  [ErrorCode.TokenExpired]: 'AccessToken 만료',
  [ErrorCode.InvalidToken]: 'AccessToken 검증 오류',
  [ErrorCode.OAuthFailed]: '소셜 Token 인증 오류',
  [ErrorCode.InvalidRefreshToken]: '유효하지 않은 RefreshToken',
  [ErrorCode.BadVideoFormat]: '비디오 포맷 오류',
  [ErrorCode.BadThumbnailFormat]: '썸네일 포맷 오류',
  [ErrorCode.NotYourVideo]: '업로더만이 요청할 수 있음',
  [ErrorCode.VideoNotFound]: '비디오를 찾을 수 없음',
};

export { ErrorCode, ErrorMessage };
