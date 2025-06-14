package com.amirmuhsin.listinghelper.networking.model.product

import kotlinx.serialization.SerialName

data class ImageModelApi(
    @SerialName("ItemId") val itemId: Long,
    @SerialName("ImageId") val imageId: Long,
    @SerialName("ImageDataType") val imageDataType: String,
    @SerialName("SalesChannelId") val salesChannelId: String,
    @SerialName("SortNumber") val sortNumber: Int,
    @SerialName("Size") val size: Long,
    @SerialName("Width") val width: Int,
    @SerialName("Height") val height: Int
)