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

    private const val ROOT_NAME = "images" // matches your <files-path name="images">

    /**
     * Copies the given [source] Uri to app-internal storage: filesDir/images/
     * Returns a FileProvider content Uri for safe future access.
     */
    fun copyToAppFiles(context: Context, source: Uri): Uri {
        val resolver = context.contentResolver
        val imagesDir = File(context.filesDir, "images").apply { if (!exists()) mkdirs() }

        // Resolve a base file name
        val display = resolveFileName(resolver, source) ?: "${UUID.randomUUID()}.jpg"

        // If no extension in display name, try from MIME
        val hasExt = display.contains('.')
        val mime = resolver.getType(source)
        val extFromMime = if (!hasExt && mime != null) {
            android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)
        } else null

        val baseName = display
            .replace(File.separatorChar, '_')
            .let { if (hasExt) it.substringBeforeLast('.') else it }
            .ifBlank { UUID.randomUUID().toString() }

        val ext = when {
            hasExt -> display.substringAfterLast('.', "")
            !extFromMime.isNullOrBlank() -> extFromMime
            else -> "jpg"
        }

        // Find a non-colliding filename
        var idx = 0
        var outFile = File(imagesDir, if (ext.isBlank()) baseName else "$baseName.$ext")
        while (outFile.exists()) {
            idx++
            val candidate = if (ext.isBlank()) "${baseName}_$idx" else "${baseName}_$idx.$ext"
            outFile = File(imagesDir, candidate)
        }

        resolver.openInputStream(source).use { inS ->
            FileOutputStream(outFile).use { outS ->
                requireNotNull(inS) { "Unable to open input stream for $source" }
                inS.copyTo(outS)
            }
        }

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

    fun deleteIfManaged(context: Context, uri: Uri): Boolean {
        val expectedAuthority = "${context.packageName}.fileprovider"
        if (uri.scheme != "content" || uri.authority != expectedAuthority) return false
        val segs = uri.pathSegments
        if (segs.isEmpty() || segs.first() != ROOT_NAME) return false

        // remaining segments form the relative path inside filesDir/images/
        val relative = segs.drop(1).joinToString("/")
        val target = File(File(context.filesDir, ROOT_NAME), relative)
        return runCatching { target.delete() }.getOrDefault(false)
    }
}
