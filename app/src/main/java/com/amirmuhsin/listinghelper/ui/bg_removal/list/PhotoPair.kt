package com.amirmuhsin.listinghelper.ui.bg_removal.list

import android.net.Uri

data class PhotoPair(
    val id: String,
    val originalUri: Uri,
    var cleanedUri: Uri?,
    var status: Status = Status.PENDING,
) {

    enum class Status {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
}



