package com.amirmuhsin.listinghelper.domain.photo

import kotlinx.coroutines.flow.Flow

interface PhotoPairLocalRepository {

    suspend fun create(product: PhotoPair): Long
    suspend fun update(product: PhotoPair)
    suspend fun updateOrder(internalId: String, order: Int)
    suspend fun delete(product: PhotoPair)

    suspend fun deleteByProductId(productId: Long)
    suspend fun getById(id: Long): PhotoPair?
    suspend fun getAllByProductId(productId: Long): List<PhotoPair>

    // WorkManager support methods
    suspend fun updateUploadStatus(internalId: String, status: PhotoPair.UploadStatus)
    suspend fun updateServerIdAndStatus(localId: String, serverId: String, status: PhotoPair.UploadStatus)
    fun getPhotoPairsForProduct(productId: Long): Flow<List<PhotoPair>>
}