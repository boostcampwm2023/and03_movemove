package com.everyone.movemove_android.ui.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

fun convertUriToBitmap(
    contentResolver: ContentResolver,
    uri: Uri,
): Bitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(
                contentResolver,
                uri
            )
        )
    } else {
        MediaStore.Images.Media.getBitmap(contentResolver, uri)
    }
}

inline fun convertImageBitmapToByteArray(
    imageBitmap: ImageBitmap,
    crossinline onSuccess: (ByteArray) -> Unit,
    crossinline onFailure: () -> Unit
) {
    val stream = ByteArrayOutputStream()

    runCatching {
        stream.use {
            imageBitmap.asAndroidBitmap().compress(
                if (Build.VERSION.SDK_INT >= 30) {
                    Bitmap.CompressFormat.WEBP_LOSSY
                } else {
                    Bitmap.CompressFormat.WEBP
                },
                50,
                stream
            )
        }
    }.onSuccess {
        onSuccess(stream.toByteArray())
    }.onFailure {
        onFailure()
    }
}

inline fun convertByteArrayToWebpFile(
    byteArray: ByteArray,
    crossinline onSuccess: (File) -> Unit,
    crossinline onFailure: () -> Unit
) {
    val webpFile = File.createTempFile("IMG_", ".webp")

    runCatching {
        FileOutputStream(webpFile).use { stream ->
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            bitmap.compress(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Bitmap.CompressFormat.WEBP_LOSSY
                } else {
                    Bitmap.CompressFormat.WEBP
                },
                50,
                stream
            )
        }
    }.onSuccess {
        onSuccess(webpFile)
    }.onFailure {
        onFailure()
    }
}