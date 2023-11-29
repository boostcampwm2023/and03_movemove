# by GPT

# 기본 이미지 설정
FROM node:16

# 작업 디렉토리 설정
WORKDIR /usr/src/app

# 의존성 파일 복사
COPY package*.json ./

# 의존성 설치
RUN yarn install

# 애플리케이션 소스 복사
COPY . .

# 앱 실행을 위한 포트 설정
EXPOSE 3000

# 애플리케이션 빌드
RUN yarn build

# 애플리케이션 실행
CMD ["node", "dist/main"]
