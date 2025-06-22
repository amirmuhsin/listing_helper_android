package com.amirmuhsin.listinghelper.data.networking.model.request

import com.google.gson.annotations.SerializedName

data class UploadProductImageRequest(
    @SerializedName("ImageData") val itemData: String,
    @SerializedName("SalesChannelId") val salesChannelId: String,
)