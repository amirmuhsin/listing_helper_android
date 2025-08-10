package com.amirmuhsin.listinghelper.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amirmuhsin.listinghelper.data.db.model.PhotoPairEntity

@Dao
interface PhotoPairDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(photos: List<PhotoPairEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(photo: PhotoPairEntity): Long

    @Query("SELECT * FROM photo_pairs_table WHERE productId = :productId ORDER BY sortOrder ASC")
    suspend fun getByProductId(productId: Long): List<PhotoPairEntity>

    @Query("DELETE FROM photo_pairs_table WHERE productId = :productId")
    suspend fun deleteByProductId(productId: Long)

    @Query("DELETE FROM photo_pairs_table WHERE id = :internalId")
    suspend fun deleteById(internalId: String)

    @Query("SELECT * FROM photo_pairs_table WHERE id = :id")
    suspend fun getById(id: Long): PhotoPairEntity?
}
