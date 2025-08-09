package com.amirmuhsin.listinghelper.data.db.mapper

import com.amirmuhsin.listinghelper.data.db.model.ProductEntity
import com.amirmuhsin.listinghelper.domain.product.Product

// Product
fun ProductEntity.toDomain() = Product(
    id, sku, isActive, name, description, shortDescription, addedTime, changedTime
)

fun Product.toEntity() = ProductEntity(
    id, sku, isActive, name, description, shortDescription, addedTime, changedTime
)