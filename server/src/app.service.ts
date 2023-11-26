import { Injectable } from '@nestjs/common';
import { xml2js } from 'xml-js';
import * as _ from 'lodash';
import { listObjects } from './ncpAPI/listObjects';
import { getObject } from './ncpAPI/getObject';

@Injectable()
export class AppService {
  async getAds() {
    const xmlData = await listObjects(process.env.ADVERTISEMENT_BUCKET);
    const jsonData: any = xml2js(xmlData, { compact: true });

    const adList = _.map(jsonData.ListBucketResult.Contents, 'Key._text');
    const adImages = await Promise.all(
      adList.map(async (ad: string) =>
        getObject(process.env.ADVERTISEMENT_BUCKET, ad),
      ),
    );

    return { adImages };
  }
}
