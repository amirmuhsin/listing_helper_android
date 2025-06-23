package com.amirmuhsin.listinghelper.data.model

data class ProductDM(
    val id: Long,
    val sku: String,
    val isActive: Boolean,
    val name: String,
    val description: String,
    val shortDescription: String,
    val addedTime: String,
    val changedTime: String
)