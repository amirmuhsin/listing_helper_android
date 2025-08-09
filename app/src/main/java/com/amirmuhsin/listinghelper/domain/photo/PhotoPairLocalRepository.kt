package com.amirmuhsin.listinghelper.domain.photo

interface PhotoPairLocalRepository {

    suspend fun create(product: PhotoPair): Long
    suspend fun update(product: PhotoPair)
    suspend fun delete(product: PhotoPair)

    suspend fun getById(id: Long): PhotoPair?
    suspend fun getAllByProductId(productId: Long): List<PhotoPair>
}