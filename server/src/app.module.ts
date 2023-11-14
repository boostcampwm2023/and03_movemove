import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { AuthModule } from './auth/auth.module';
import { UserModule } from './user/user.module';
import { VideoModule } from './video/video.module';
import { ActionModule } from './action/action.module';

@Module({
  imports: [AuthModule, UserModule, VideoModule, ActionModule],
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {}
