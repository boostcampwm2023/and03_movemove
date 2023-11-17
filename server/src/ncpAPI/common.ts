import * as CryptoJS from 'crypto-js';

export function makeSignature(method: string, url: string, timestamp: number) {
  const space = ' '; // one space
  const newLine = '\n'; // new line
  const accessKey = process.env.ACCESS_KEY!;
  const secretKey = process.env.SECRET_KEY!;

  const hmac = CryptoJS.algo.HMAC.create(CryptoJS.algo.SHA256, secretKey);
  hmac.update(method);
  hmac.update(space);
  hmac.update(url);
  hmac.update(newLine);
  hmac.update(`${timestamp}`);
  hmac.update(newLine);
  hmac.update(accessKey);

  const hash = hmac.finalize();

  return hash.toString(CryptoJS.enc.Base64);
}

export const getAuthorization = (headers: object): string => {
  const accessKeyID = process.env.ACCESS_KEY!;
  const region = 'kr-standard';

  const date = getDateStamp();

  const scope = `${date}/${region}/s3/aws4_request`;

  const signedHeaders = Object.keys(headers).join(';');

  const kSignature = createSignatureKey(scope, headers);
  const authorization = `AWS4-HMAC-SHA256 Credential=${accessKeyID}/${scope}, SignedHeaders=${signedHeaders}, Signature=${kSignature}`;
  return authorization;
};

const createSignatureKey = (scope: string, headers: object): string => {
  const kSigning = createSigningKey();
  const stringToSign = createStringToSign(scope, headers);
  const kSignature = CryptoJS.HmacSHA256(stringToSign, kSigning).toString(
    CryptoJS.enc.Hex,
  );
  return kSignature;
};

const createSigningKey = () => {
  const region = 'kr-standard';
  const kSecret = process.env.SECRET_KEY!;

  const kDate = getHash('AWS4' + kSecret, getDateStamp());
  const kRegion = getHash(kDate, region);
  const kService = getHash(kRegion, 's3');
  const kSigning = getHash(kService, 'aws4_request');

  return kSigning;
};

const getHash = (key: any, message: string) => {
  console.log(key);
  console.log(message);
  return CryptoJS.HmacSHA256(message, key);
  // return crypto.createHmac('sha256', key).update(message).digest();
};

const createStringToSign = (scope: string, headers: object) => {
  const timeStamp = getTimeStamp();
  const method = 'PUT';
  const canonicalURI = '/video-thimbnail-bucket/sample-object.txt';
  const canonicalRequest = createCanonicalRequest(
    method,
    canonicalURI,
    headers,
  );

  const stringToSign = `AWS4-HMAC-SHA256
${timeStamp}
${scope}
${CryptoJS.enc.Hex.stringify(CryptoJS.SHA256(canonicalRequest))}`;

  return stringToSign;
};

const createCanonicalRequest = (
  method: string,
  path: string,
  headers: object,
) => {
  const hashedPayLoad = 'UNSIGNED-PAYLOAD';
  const { canonicalHeaders, signedHeaders } = getHeaders(headers);
  const canonicalRequest = `${method}
${path}

${canonicalHeaders}

${signedHeaders}
${hashedPayLoad}`;
  console.log(`canonicalRequest : ${canonicalRequest}`);
  return canonicalRequest;
};

const getHeaders = (headers: object) => {
  const canonicalHeaders = Object.entries(headers)
    .map(([key, value]) => `${key}:${value}`)
    .join('\n');
  const signedHeaders = Object.keys(headers).join(';');
  return { canonicalHeaders, signedHeaders };
};

const getDateStamp = (): string => {
  const today = new Date();
  const year = today.getFullYear();
  const month = today.getMonth() + 1; // 한자리 수 예외 처리 필요
  const day = today.getDate();

  const date = `${year}${month}${day}`;
  return date;
};

export const getTimeStamp = (): string => {
  const today = new Date();
  const timeStamp = today
    .toISOString()
    .replace(/[:-]/g, '')
    .replace(/\.\d{3}Z$/, 'Z'); // by GPT
  return timeStamp;
};
