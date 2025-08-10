package com.amirmuhsin.listinghelper.data.db.mapper

import com.amirmuhsin.listinghelper.data.db.model.ProductEntity
import com.amirmuhsin.listinghelper.domain.product.Product

// Product
fun ProductEntity.toDomain() = Product(
    id, serverId, sku, isActive, name, description, shortDescription, addedTime, changedTime, Product.Status.valueOf(status), totalImageCount
)

fun Product.toEntity() = ProductEntity(
    id, serverId, sku, isActive, name, description, shortDescription, addedTime, changedTime, status.name, totalImageCount
)