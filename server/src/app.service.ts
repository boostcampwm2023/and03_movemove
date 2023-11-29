import { Injectable } from '@nestjs/common';
import { listObjects } from './ncpAPI/listObjects';
import { getObject } from './ncpAPI/getObject';

@Injectable()
export class AppService {
  async getAds() {
    const adList = await listObjects(process.env.ADVERTISEMENT_BUCKET);
    const adImages = await Promise.all(
      adList.map(async (ad: string) =>
        getObject(process.env.ADVERTISEMENT_BUCKET, ad),
      ),
    );

    return { adImages };
  }
}
