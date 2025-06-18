package com.amirmuhsin.listinghelper.util

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri

fun getImageSizeInBytes(context: Context, uri: Uri): Long {
    return context.contentResolver.openAssetFileDescriptor(uri, "r")?.use {
        it.length
    } ?: -1
}

fun getImageResolution(context: Context, uri: Uri): Pair<Int, Int>? {
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
    val readableSize = if (fileSizeBytes > 0) {
        "%.2f MB".format(fileSizeBytes / 1024f / 1024f)
    } else "Unknown"
    return readableSize
}
