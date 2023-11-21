package com.everyone.movemove_android.ui.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

fun getVideoFilePath(context: Context, uri: Uri): String? {
    val projection = arrayOf(MediaStore.Video.Media.DATA)
    val cursor = context.contentResolver.query(uri, projection, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            return it.getString(columnIndex)
        }
    }

    return null
}