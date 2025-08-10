package com.amirmuhsin.listinghelper.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amirmuhsin.listinghelper.data.db.model.ProductEntity

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(product: ProductEntity): Long

    @Query("SELECT * FROM product_table WHERE id = :productId")
    suspend fun getById(productId: Long): ProductEntity?

    @Query("SELECT * FROM product_table ORDER BY addedTime DESC")
    suspend fun getAll(): List<ProductEntity>

    @Query("DELETE FROM product_table WHERE id = :productId")
    suspend fun delete(productId: Long)

    @Query("UPDATE product_table SET status = :status WHERE id = :productId")
    suspend fun updateStatus(productId: Long, status: String)

    @Query("UPDATE product_table SET totalImageCount = :count WHERE id = :productId")
    suspend fun updateImageCount(productId: Long, count: Int)


}
