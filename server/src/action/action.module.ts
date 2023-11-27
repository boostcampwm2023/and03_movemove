import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { Video, VideoSchema } from 'src/video/schemas/video.schema';
import { User, UserSchema } from 'src/user/schemas/user.schema';
import { ActionController } from './action.controller';
import { ActionService } from './action.service';

@Module({
  imports: [
    MongooseModule.forFeature([{ name: Video.name, schema: VideoSchema }]),
    MongooseModule.forFeature([{ name: User.name, schema: UserSchema }]),
  ],
  controllers: [ActionController],
  providers: [ActionService],
})
export class ActionModule {}
