package com.amirmuhsin.listinghelper.domain.model

data class Product(
    val id: Long,
    val sku: String,
    val isActive: Boolean,
    val name: String,
    val description: String,
    val shortDescription: String,
    val addedTime: String,
    val changedTime: String
)