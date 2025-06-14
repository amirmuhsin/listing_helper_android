package com.amirmuhsin.listinghelper.networking.model.product

import kotlinx.serialization.SerialName

data class ProductModelApi(
    @SerialName("Id") val id: Long,
    @SerialName("SKU") val sku: String,
    @SerialName("IsActive") val isActive: Boolean,
    @SerialName("Name") val name: String,
    @SerialName("Description") val description: String,
    @SerialName("ShortDescription") val shortDescription: String,
    @SerialName("Added") val addedTime: String,
    @SerialName("Changed") val changedTime: String,
)