package com.amirmuhsin.listinghelper.ui.bg_removal.list

import android.graphics.Bitmap
import android.net.Uri

data class PhotoPair(
    val id: String,
    val originalUri: Uri,
    var cleanedBitmap: Bitmap?
)

