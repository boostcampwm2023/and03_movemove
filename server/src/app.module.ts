import { Module } from '@nestjs/common';
import { AuthModule } from './auth/auth.module';
import { VideoModule } from './video/video.module';
import { ActionModule } from './action/action.module';
import { UserModule } from './user/user.module';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { ConfigModule } from '@nestjs/config';
@Module({
  imports: [
    ConfigModule.forRoot(),
    AuthModule,
    VideoModule,
    ActionModule,
    UserModule,
  ],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
