package com.amirmuhsin.listinghelper.networking.model.request

import kotlinx.serialization.SerialName

data class UploadProductImageRequest(
    @SerialName("ImageData") val itemData: String,
    @SerialName("SalesChannelId") val salesChannelId: String,
)