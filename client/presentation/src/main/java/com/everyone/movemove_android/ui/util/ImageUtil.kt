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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.net.toUri
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


fun Uri.toImageBitmap(contentResolver: ContentResolver): ImageBitmap {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(
                contentResolver,
                this
            )
        ).asImageBitmap()
    } else {
        MediaStore.Images.Media.getBitmap(contentResolver, this).asImageBitmap()
    }
}

fun ImageBitmap.toWebpFile(isCompressNeeded: Boolean = true): File {
    val stream = ByteArrayOutputStream()

    stream.use {
        this.asAndroidBitmap().compress(
            if (Build.VERSION.SDK_INT >= 30) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                Bitmap.CompressFormat.WEBP
            },
            if (isCompressNeeded) 50 else 100,
            stream
        )
    }

    return stream.toByteArray().toWebpFile()
}

private fun ByteArray.toWebpFile(): File {
    val webpFile = File.createTempFile("IMG_", ".webp")

    FileOutputStream(webpFile).use { stream ->
        val bitmap = BitmapFactory.decodeByteArray(this, 0, this.size)
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

    return webpFile
}

fun ImageBitmap.toUri(): Uri = this.toWebpFile().toUri()