import axios from 'axios';
import { getTimeStamp, getAuthorization } from './common';

export const getObject = (bucketName: string, objectName: string) => {
  const endPoint = 'https://kr.object.ncloudstorage.com';
  const apiUrl = `${endPoint}/${bucketName}/${objectName}`;

  const timeStamp = getTimeStamp();
  const defaultHeaders = {
    host: 'kr.object.ncloudstorage.com',
    'x-amz-content-sha256': 'UNSIGNED-PAYLOAD',
    'x-amz-date': timeStamp,
  };

  const method = 'GET';
  return axios.get(apiUrl, {
    headers: {
      Authorization: getAuthorization(method, defaultHeaders, timeStamp),
      ...defaultHeaders,
    },
  });
};
