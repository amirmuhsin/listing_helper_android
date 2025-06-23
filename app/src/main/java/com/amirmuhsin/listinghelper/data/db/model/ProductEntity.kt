package com.amirmuhsin.listinghelper.data.db.model

data class ProductEntity(
    val id: Long,
    val sku: String,
    val isActive: Boolean,
    val name: String,
    val description: String,
    val shortDescription: String,
    val addedTime: String,
    val changedTime: String
)