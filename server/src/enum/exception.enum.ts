enum ErrorCodeEnum {
  UserConflict = 3001,
  BadRequest = 2000,
  LoginFail = 1000,
  TokenExpired = 1001,
  InvalidToken = 1002,
  InvalidUserID = 1003,
  BadTokenFormat = 1004,
  OAuthFailed = 1005,
}

const ErrorMessage = {
  [ErrorCodeEnum.UserConflict]: '이미 가입된 회원',
  [ErrorCodeEnum.BadRequest]: '잘못된 요청 형식',
  [ErrorCodeEnum.LoginFail]: '가입되지 않은 회원',
  [ErrorCodeEnum.TokenExpired]: 'AccessToken 만료',
  [ErrorCodeEnum.BadTokenFormat]: 'AccessToken 형식 오류',
  [ErrorCodeEnum.InvalidToken]: 'AccessToken 검증 오류',
  [ErrorCodeEnum.InvalidUserID]: '잘못된 userID',
  [ErrorCodeEnum.OAuthFailed]: 'userID 검증 오류',
};

const ErrorDescription = {
  [ErrorCodeEnum.UserConflict]: '회원 userID가 중복됨',
  [ErrorCodeEnum.BadRequest]: 'Format 오류, 관리자에게 문의',
  [ErrorCodeEnum.LoginFail]: 'userID가 없음',
  [ErrorCodeEnum.TokenExpired]: 'AccessToken 재발급 필요',
  [ErrorCodeEnum.BadTokenFormat]: 'AccessToken의 Payload에 userID가 없음',
  [ErrorCodeEnum.OAuthFailed]: '소셜 토큰이 유효하지 않거나 userID가 다름',
  [ErrorCodeEnum.InvalidUserID]: 'AccessToken내 userID를 가진 유저가 없음',
  [ErrorCodeEnum.InvalidToken]:
    'AccessToken이 다른 시크릿키로 발급되었거나 위변조됨',
};
export { ErrorCodeEnum, ErrorMessage, ErrorDescription };
