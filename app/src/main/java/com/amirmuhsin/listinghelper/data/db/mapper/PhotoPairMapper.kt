package com.amirmuhsin.listinghelper.data.db.mapper

import android.net.Uri
import com.amirmuhsin.listinghelper.data.db.model.PhotoPairEntity
import com.amirmuhsin.listinghelper.domain.photo.PhotoPair

fun PhotoPairEntity.toDomain() = PhotoPair(
    internalId = id,
    productId = productId,
    originalUri = originalUri?.let(Uri::parse) ?: Uri.EMPTY,
    cleanedUri = cleanedUri?.let(Uri::parse),
    bgCleanStatus = PhotoPair.BgCleanStatus.valueOf(bgCleanStatus),
    order = sortOrder,
    uploadStatus = PhotoPair.UploadStatus.valueOf(uploadStatus)
)

fun PhotoPair.toEntity() = PhotoPairEntity(
    id = internalId,
    productId = productId,
    originalUri = originalUri.toString(),
    cleanedUri = cleanedUri?.toString(),
    bgCleanStatus = bgCleanStatus.name,
    sortOrder = order,
    uploadStatus = uploadStatus.name
)
