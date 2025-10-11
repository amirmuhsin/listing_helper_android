package com.amirmuhsin.listinghelper.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amirmuhsin.listinghelper.data.db.model.PhotoPairEntity
import kotlinx.coroutines.flow.Flow

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

    @Query("SELECT * FROM photo_pairs_table WHERE id = :internalId")
    suspend fun getById(internalId: Long): PhotoPairEntity?

    @Query("UPDATE photo_pairs_table SET sortOrder = :order WHERE id = :internalId")
    suspend fun updateOrder(internalId: String, order: Int)

    @Query("UPDATE photo_pairs_table SET uploadStatus = :status WHERE id = :internalId")
    suspend fun updateUploadStatus(internalId: String, status: String)

    @Query("UPDATE photo_pairs_table SET uploadItemId = :serverId, uploadStatus = :status WHERE id = :localId")
    suspend fun updateServerIdAndStatus(localId: String, serverId: String, status: String)

    @Query("SELECT * FROM photo_pairs_table WHERE productId = :productId ORDER BY sortOrder ASC")
    fun observeByProductId(productId: Long): Flow<List<PhotoPairEntity>>
}
