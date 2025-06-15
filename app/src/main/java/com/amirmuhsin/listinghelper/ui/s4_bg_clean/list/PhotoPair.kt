package com.amirmuhsin.listinghelper.ui.s4_bg_clean.list

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoPair(
    val id: String,
    val originalUri: Uri,
    var cleanedUri: Uri?,
    var status: Status = Status.PENDING,
): Parcelable {

    enum class Status {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
}