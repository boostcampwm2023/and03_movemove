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
