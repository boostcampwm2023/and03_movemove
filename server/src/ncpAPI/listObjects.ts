import axios from 'axios';
import { xml2js } from 'xml-js';
import * as _ from 'lodash';
import { getTimeStamp, getAuthorization } from './common';

// TODO CanonicalQueryString 추가해서 개수 제한
export const listObjects = async (bucketName: string, params = {}) => {
  const endPoint = 'https://kr.object.ncloudstorage.com';
  const canonicalURI = `/${bucketName}`;
  const apiUrl = `${endPoint}${canonicalURI}`;

  const timeStamp = getTimeStamp();
  const defaultHeaders = {
    accept: 'application/json',
    host: 'kr.object.ncloudstorage.com',
    'x-amz-content-sha256': 'UNSIGNED-PAYLOAD',
    'x-amz-date': timeStamp,
  };

  const method = 'GET';
  Object.assign(params, { 'list-type': 2 });
  return axios
    .get(apiUrl, {
      headers: {
        Authorization: getAuthorization(
          method,
          canonicalURI,
          defaultHeaders,
          timeStamp,
          params,
        ),
        ...defaultHeaders,
      },
      params,
    })
    .then((response): any => xml2js(response.data, { compact: true }))
    .then((element) => {
      const contents = element.ListBucketResult.Contents;
      if (Array.isArray(contents)) {
        return _.map(contents, 'Key._text');
      }
      if (contents) {
        // eslint-disable-next-line no-underscore-dangle
        return [contents.Key._text];
      }
      return [];
    });
};
export const checkUpload = async (bucketName: string, objectName: string) => {
  const objectList = await listObjects(bucketName, { prefix: objectName });

  return objectList.includes(objectName);
};
