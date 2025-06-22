package com.amirmuhsin.listinghelper.domain.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoPair(
    val internalId: String,
    val originalUri: Uri,
    var cleanedUri: Uri?,
    var bgCleanStatus: BgCleanStatus = BgCleanStatus.PENDING,
    var order: Int,
    var uploadStatus: UploadStatus = UploadStatus.PENDING,
): Parcelable {

    enum class BgCleanStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }

    enum class UploadStatus {
        PENDING,
        UPLOADING,
        UPLOADED,
        FAILED
    }
}