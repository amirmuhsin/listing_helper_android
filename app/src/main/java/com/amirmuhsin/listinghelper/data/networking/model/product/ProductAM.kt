package com.amirmuhsin.listinghelper.data.networking.model.product

import com.google.gson.annotations.SerializedName

data class ProductAM(
    @SerializedName("Id") val id: Long,
    @SerializedName("SKU") val sku: String,
    @SerializedName("IsActive") val isActive: Boolean,
    @SerializedName("Name") val name: String,
    @SerializedName("Description") val description: String,
    @SerializedName("ShortDescription") val shortDescription: String,
    @SerializedName("Added") val addedTime: String,
    @SerializedName("Changed") val changedTime: String,
)