package com.amirmuhsin.listinghelper.domain.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoPair(
    val internalId: String,
    val originalUri: Uri,
    var cleanedUri: Uri?,
    var status: Status = Status.PENDING,
    var order: Int,
    var isUploaded: Boolean = false
): Parcelable {

    enum class Status {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
}