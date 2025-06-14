package com.amirmuhsin.listinghelper.networking.model.product

import com.google.gson.annotations.SerializedName

data class ImageAM(
    @SerializedName("ItemId") val itemId: Long,
    @SerializedName("ImageId") val imageId: Long,
    @SerializedName("ImageDataType") val imageDataType: String,
    @SerializedName("SalesChannelId") val salesChannelId: String,
    @SerializedName("SortNumber") val sortNumber: Int,
    @SerializedName("Size") val size: Long,
    @SerializedName("Width") val width: Int,
    @SerializedName("Height") val height: Int
)