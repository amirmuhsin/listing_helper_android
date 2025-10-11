package com.amirmuhsin.listinghelper.repository

import com.amirmuhsin.listinghelper.data.db.dao.PhotoPairDao
import com.amirmuhsin.listinghelper.data.db.mapper.toDomain
import com.amirmuhsin.listinghelper.data.db.mapper.toEntity
import com.amirmuhsin.listinghelper.domain.photo.PhotoPair
import com.amirmuhsin.listinghelper.domain.photo.PhotoPairLocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PhotoPairLocalRepositoryImpl(
    private val photoPairDao: PhotoPairDao
): PhotoPairLocalRepository {

    override suspend fun create(photoPair: PhotoPair): Long = withContext(Dispatchers.IO) {
        val photoPairEntity = photoPair.toEntity()
        val createdPhotoPairId = photoPairDao.upsert(photoPairEntity)
        createdPhotoPairId
    }

    override suspend fun update(photoPair: PhotoPair) = withContext(Dispatchers.IO) {
        val photoPairEntity = photoPair.toEntity()
        photoPairDao.upsert(photoPairEntity)
        Unit
    }

    override suspend fun updateOrder(internalId: String, order: Int) {
        withContext(Dispatchers.IO) {
            photoPairDao.updateOrder(internalId, order)
        }
    }

    override suspend fun delete(photoPair: PhotoPair) {
        withContext(Dispatchers.IO) {
            photoPairDao.deleteById(photoPair.internalId)
        }
    }

    override suspend fun deleteByProductId(productId: Long) {
        withContext(Dispatchers.IO) {
            photoPairDao.deleteByProductId(productId)
        }
    }

    override suspend fun getById(id: Long): PhotoPair? {
        return withContext(Dispatchers.IO) {
            val entity = photoPairDao.getById(id)
            entity?.toDomain()
        }
    }

    override suspend fun getAllByProductId(productId: Long): List<PhotoPair> {
        return withContext(Dispatchers.IO) {
            photoPairDao.getByProductId(productId).map { it.toDomain() }
        }
    }

    override suspend fun updateUploadStatus(internalId: String, status: PhotoPair.UploadStatus) {
        withContext(Dispatchers.IO) {
            photoPairDao.updateUploadStatus(internalId, status.name)
        }
    }

    override suspend fun updateServerIdAndStatus(localId: String, serverId: String, status: PhotoPair.UploadStatus) {
        withContext(Dispatchers.IO) {
            photoPairDao.updateServerIdAndStatus(localId, serverId, status.name)
        }
    }

    override fun getPhotoPairsForProduct(productId: Long): Flow<List<PhotoPair>> {
        return photoPairDao.observeByProductId(productId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

}