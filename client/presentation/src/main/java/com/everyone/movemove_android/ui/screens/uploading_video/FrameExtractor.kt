package com.everyone.movemove_android.ui.screens.uploading_video

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaFormat.KEY_HEIGHT
import android.media.MediaFormat.KEY_ROTATION
import android.media.MediaFormat.KEY_WIDTH
import io.ktor.util.moveToByteArray
import java.io.ByteArrayOutputStream


class FrameExtractor(private val path: String) {
    private val filteredPaint by lazy {
        val colorMatrix = ColorMatrix(
            floatArrayOf(
                0f, 0f, 1f, 0f, 0f, // R값과 B값 교환
                0f, 1f, 0f, 0f, 0f, // G값 그대로 유지
                1f, 0f, 0f, 0f, 0f, // B값과 R값 교환
                0f, 0f, 0f, 1f, 0f // A값 그대로 유지
            )
        )
        val colorFilter = ColorMatrixColorFilter(colorMatrix)
        Paint().apply { setColorFilter(colorFilter) }
    }

    fun getThumbnails(duration: Long, onGetBitmap: (Bitmap) -> Unit) {
        val mediaExtractor = MediaExtractor().apply {
            setDataSource(path)
        }

        var mimeType: String? = null
        var format: MediaFormat? = null
        for (i in 0 until mediaExtractor.trackCount) {
            format = mediaExtractor.getTrackFormat(i)
            mimeType = format.getString(MediaFormat.KEY_MIME)
            if (mimeType?.startsWith(MIME_VIDEO) == true) {
                mediaExtractor.selectTrack(i)
                break
            }
        }

        if (mimeType == null) {
            return
        }

        val decoder = MediaCodec.createDecoderByType(mimeType).apply {
            configure(format, null, null, 0)
            start()
        }

        with(decoder) {
            val width = outputFormat.getInteger(KEY_WIDTH)
            val height = outputFormat.getInteger(KEY_HEIGHT)
            val rotation = outputFormat.getInteger(KEY_ROTATION)
            val bufferInfo = MediaCodec.BufferInfo()
            val unit = duration * 1000L / THUMBNAIL_COUNT

            for (i in 1..THUMBNAIL_COUNT) {
                mediaExtractor.seekTo(unit * i, MediaExtractor.SEEK_TO_CLOSEST_SYNC)

                val inputBufferIndex = dequeueInputBuffer(INPUT_BUFFER_TIMEOUT)
                if (inputBufferIndex >= 0) {
                    val inputBuffer = getInputBuffer(inputBufferIndex)

                    inputBuffer?.let { inputBuffer ->
                        val sampleSize = mediaExtractor.readSampleData(inputBuffer, 0)
                        if (sampleSize > 0) {
                            queueInputBuffer(inputBufferIndex, 0, sampleSize, mediaExtractor.sampleTime, 0)
                        }

                        var outputBufferIndex = dequeueOutputBuffer(bufferInfo, OUTPUT_BUFFER_TIMEOUT)

                        while (outputBufferIndex < 0) {
                            outputBufferIndex = dequeueOutputBuffer(bufferInfo, OUTPUT_BUFFER_TIMEOUT)
                        }

                        val outputBuffer = getOutputBuffer(outputBufferIndex)
                        outputBuffer?.let { byteBuffer ->
                            ByteArrayOutputStream().use { stream ->
                                val yuvImage = YuvImage(byteBuffer.moveToByteArray(), ImageFormat.NV21, width, height, null)
                                yuvImage.compressToJpeg(
                                    Rect(0, 0, width, height),
                                    100,
                                    stream
                                )

                                onGetBitmap(
                                    createScaledBitmap(
                                        width = width,
                                        height = height,
                                        rotation = rotation,
                                        stream = stream,
                                    )
                                )
                            }
                        }

                        releaseOutputBuffer(outputBufferIndex, false)
                    }
                }
            }

            stop()
            release()
        }

        mediaExtractor.release()
    }

    private fun createScaledBitmap(
        width: Int,
        height: Int,
        rotation: Int,
        stream: ByteArrayOutputStream,
    ): Bitmap {
        var originalBitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())
        if (rotation != 0) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, width, height, Matrix().apply { postRotate(rotation.toFloat()) }, true)
        }
        val filteredBitmap = Bitmap.createBitmap(
            if (rotation == 0) width else height,
            if (rotation == 0) height else width,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(filteredBitmap)
        canvas.drawBitmap(originalBitmap, 0f, 0f, filteredPaint)

        return Bitmap.createScaledBitmap(
            filteredBitmap,
            filteredBitmap.width / 4,
            filteredBitmap.height / 4,
            false
        )
    }

    companion object {
        private const val MIME_VIDEO = "video/"
        private const val THUMBNAIL_COUNT = 15
        private const val INPUT_BUFFER_TIMEOUT = 1000L
        private const val OUTPUT_BUFFER_TIMEOUT = -1L
    }
}