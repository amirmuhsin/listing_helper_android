package com.amirmuhsin.listinghelper.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageStore {

    /**
     * Copies the given [source] Uri to app-internal storage: filesDir/images/
     * Returns a FileProvider content Uri for safe future access.
     */
    fun copyToAppFiles(context: Context, source: Uri): Uri {
        val resolver = context.contentResolver
        val imagesDir = File(context.filesDir, "images").apply { if (!exists()) mkdirs() }

        val fileName = resolveFileName(resolver, source) ?: "${UUID.randomUUID()}.jpg"
        val safeName = fileName.replace(File.separatorChar, '_')
        val outFile = File(imagesDir, safeName)

        resolver.openInputStream(source).use { inS ->
            FileOutputStream(outFile).use { outS ->
                requireNotNull(inS) { "Unable to open input stream for $source" }
                inS.copyTo(outS)
            }
        }

        // Return a FileProvider Uri
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            outFile
        )
    }

    private fun resolveFileName(resolver: ContentResolver, uri: Uri): String? {
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
                if (c.moveToFirst()) return c.getString(0)
            }
        }
        // Fallback to lastPathSegment
        return uri.lastPathSegment?.substringAfterLast('/')
    }
}
