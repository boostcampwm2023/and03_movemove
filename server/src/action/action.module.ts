import { Module } from '@nestjs/common';
import { ActionController } from './action.controller';
import { ActionService } from './action.service';

@Module({
  controllers: [ActionController],
  providers: [ActionService],
})
export class ActionModule {}
