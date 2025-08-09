package com.amirmuhsin.listinghelper.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_pairs_table")
data class PhotoPairEntity(
    @PrimaryKey val id: String,
    val productId: Long,
    val originalUri: String?,
    val cleanedUri: String?,
    val bgCleanStatus: String,
    val sortOrder: Int,
    val uploadStatus: String
)