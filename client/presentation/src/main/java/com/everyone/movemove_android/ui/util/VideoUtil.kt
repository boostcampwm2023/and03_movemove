package com.everyone.movemove_android.ui.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

inline fun getVideoFilePath(
    context: Context,
    videoUri: Uri,
    crossinline onSuccess: (String) -> Unit,
    crossinline onFailure: () -> Unit
) {
    val projection = arrayOf(MediaStore.Video.Media.DATA)
    val cursor = context.contentResolver.query(
        videoUri,
        projection,
        null,
        null,
        null
    )

    cursor?.use {
        try {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                onSuccess(it.getString(columnIndex))
            }
        } catch (e: Exception) {
            onFailure()
        }
    }
}