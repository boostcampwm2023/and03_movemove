import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { MongooseModule } from '@nestjs/mongoose';
import { AuthModule } from './auth/auth.module';
import { VideoModule } from './video/video.module';
import { ActionModule } from './action/action.module';
import { UserModule } from './user/user.module';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { PresignedUrlController } from './presigned-url/presigned-url.controller';
import { PresignedUrlService } from './presigned-url/presigned-url.service';
import { PresignedUrlModule } from './presigned-url/presigned-url.module';

@Module({
  imports: [
    ConfigModule.forRoot(),
    MongooseModule.forRoot(process.env.MONGODB_URI),
    AuthModule,
    VideoModule,
    ActionModule,
    UserModule,
    PresignedUrlModule,
  ],
  controllers: [AppController, PresignedUrlController],
  providers: [AppService, PresignedUrlService],
})
export class AppModule {}
