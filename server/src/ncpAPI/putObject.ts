import axios from 'axios';
import { getTimeStamp, getAuthorization } from './common';

export const putObject = (bucketName: string, objectName: string, data) => {
  const endPoint = 'https://kr.object.ncloudstorage.com';
  const apiUrl = `${endPoint}/${bucketName}/${objectName}`;

  const timeStamp = getTimeStamp();
  const defaultHeaders = {
    host: 'kr.object.ncloudstorage.com',
    'x-amz-content-sha256': 'UNSIGNED-PAYLOAD',
    'x-amz-date': timeStamp,
  };

  return axios.put(apiUrl, data, {
    headers: {
      Authorization: getAuthorization(defaultHeaders),
      ...defaultHeaders,
    },
  });
};
