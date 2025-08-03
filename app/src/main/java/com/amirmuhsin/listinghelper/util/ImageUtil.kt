package com.amirmuhsin.listinghelper.util

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri

fun getImageSizeInBytes(context: Context, uri: Uri?): Long {
    if (uri == null) return -1
    return try {
        context.contentResolver.openAssetFileDescriptor(uri, "r")?.use {
            it.length
        } ?: -1
    } catch (e: Exception) {
        e.printStackTrace()
        -1
    }
}

fun getImageResolution(context: Context, uri: Uri?): Pair<Int, Int>? {
    if (uri == null) return null
    return try {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        }

        if (options.outWidth > 0 && options.outHeight > 0) {
            Pair(options.outWidth, options.outHeight)
        } else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getReadableSize(fileSizeBytes: Long): String {
    return when {
        fileSizeBytes <= 0 -> "Unknown"
        fileSizeBytes < 1024 -> "$fileSizeBytes B"
        fileSizeBytes < 1024 * 1024 -> "%.1f KB".format(fileSizeBytes / 1024f)
        else -> "%.2f MB".format(fileSizeBytes / 1024f / 1024f)
    }
}
