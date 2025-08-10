package com.amirmuhsin.listinghelper.domain.photo

interface PhotoPairLocalRepository {

    suspend fun create(product: PhotoPair): Long
    suspend fun update(product: PhotoPair)
    suspend fun updateOrder(internalId: String, order: Int)
    suspend fun delete(product: PhotoPair)

    suspend fun deleteByProductId(productId: Long)
    suspend fun getById(id: Long): PhotoPair?
    suspend fun getAllByProductId(productId: Long): List<PhotoPair>
}