package com.amirmuhsin.listinghelper.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_table")
data class ProductEntity(
    @PrimaryKey val id: Long,
    val sku: String,
    val isActive: Boolean,
    val name: String,
    val description: String,
    val shortDescription: String,
    val addedTime: String,
    val changedTime: String
)