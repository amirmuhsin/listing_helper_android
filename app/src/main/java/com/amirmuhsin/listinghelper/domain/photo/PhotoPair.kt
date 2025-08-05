package com.amirmuhsin.listinghelper.domain.photo

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoPair(
    val internalId: String,
    val originalUri: Uri,
    val cleanedUri: Uri?,
    val bgCleanStatus: BgCleanStatus = BgCleanStatus.PENDING,
    val order: Int,
    val uploadStatus: UploadStatus = UploadStatus.PENDING,
): Parcelable, PhotoItem() {

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