import { PipeTransform, Injectable } from '@nestjs/common';
import {
  ThumbnailFormatException,
  VideoFormatException,
} from 'src/exceptions/base.exception';

const SupportedVideoMimeTypes = new Set([
  'video/x-msvideo', // avi
  'video/quicktime', // mov
  'video/mp4', // mp4
  'audio/mpeg', // mp3
  'video/3gpp', // 3gp
  'video/mpeg', // mpg, mpeg
  'video/x-m4v', // m4v
  'video/x-ms-vob', // vob
  'video/x-ms-wmv', // wmv
  'video/x-ms-asf', // asf
  'video/x-matroska', // mkv
  'video/x-flv', // flv
  'video/webm', // webm
  'image/gif', // gif
  'video/av01', // av1
]);

const SupportedImageMimeTypes = new Set([
  'image/jpeg', // JPEG
  'image/jpg', // JPG
  'image/png', // PNG
  'image/gif', // GIF
  'image/bmp', // BMP
  'image/svg+xml', // SVG
  'image/tiff', // TIFF
  'image/webp', // WEBP
  'image/x-icon', // ICO
  'image/vnd.microsoft.icon', // ICO
]);

@Injectable()
export class FileExtensionPipe implements PipeTransform {
  transform(files: any) {
    if (!files.video || !SupportedVideoMimeTypes.has(files.video[0].mimetype))
      throw new VideoFormatException();
    if (
      !files.thumbnail ||
      !SupportedImageMimeTypes.has(files.thumbnail[0].mimetype)
    )
      throw new ThumbnailFormatException();
    return files;
  }
}
