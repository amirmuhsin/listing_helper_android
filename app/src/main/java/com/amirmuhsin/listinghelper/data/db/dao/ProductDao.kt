package com.amirmuhsin.listinghelper.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amirmuhsin.listinghelper.data.db.model.ProductEntity

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(product: ProductEntity)

    @Query("SELECT * FROM product_table WHERE id = :productId")
    suspend fun getById(productId: Long): ProductEntity?

    @Query("SELECT * FROM product_table ORDER BY addedTime DESC")
    suspend fun getAll(): List<ProductEntity>

    @Query("DELETE FROM product_table WHERE id = :productId")
    suspend fun delete(productId: Long)
}
