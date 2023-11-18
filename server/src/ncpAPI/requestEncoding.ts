import axios from 'axios';
import { makeSignature } from './common';
export async function requestEncoding(bucketName: string, pathList: string[]) {
  const timestamp = Date.now();
  const accessKey = process.env.ACCESS_KEY!;
  const categoryId = process.env.CATEGORY_ID!;
  const endPoint = 'https://vodstation.apigw.ntruss.com';
  const canonicalURI = `/api/v2/category/${categoryId}/add-files`;
  const apiUrl = `${endPoint}${canonicalURI}`;
  const method = 'PUT';
  const sign = makeSignature(method, canonicalURI, timestamp);

  return axios({
    method,
    url: apiUrl,
    headers: {
      'x-ncp-apigw-timestamp': timestamp,
      'x-ncp-iam-access-key': accessKey,
      'x-ncp-apigw-signature-v2': sign,
      'Content-Type': 'application/json',
    },
    data: JSON.stringify({
      bucketName,
      pathList,
    }),
  });
}
