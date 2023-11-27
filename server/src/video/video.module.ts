import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { User, UserSchema } from 'src/user/schemas/user.schema';
import { ActionService } from 'src/action/action.service';
import { VideoController } from './video.controller';
import { VideoService } from './video.service';
import { Video, VideoSchema } from './schemas/video.schema';
import { FileExtensionPipe } from './video.pipe';

@Module({
  imports: [
    MongooseModule.forFeature([{ name: Video.name, schema: VideoSchema }]),
    MongooseModule.forFeature([{ name: User.name, schema: UserSchema }]),
  ],
  controllers: [VideoController],
  providers: [VideoService, ActionService, FileExtensionPipe],
})
export class VideoModule {}
