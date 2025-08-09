package com.amirmuhsin.listinghelper.repository

import com.amirmuhsin.listinghelper.data.db.dao.PhotoPairDao
import com.amirmuhsin.listinghelper.data.db.dao.ProductDao
import com.amirmuhsin.listinghelper.domain.photo.PhotoPair
import com.amirmuhsin.listinghelper.domain.photo.PhotoPairLocalRepository

class PhotoPairLocalRepositoryImpl(
    private val productDao: ProductDao,
    private val photoPairDao: PhotoPairDao
): PhotoPairLocalRepository {

    override suspend fun create(product: PhotoPair): Long {
        TODO("Not yet implemented")
    }

    override suspend fun update(product: PhotoPair) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(product: PhotoPair) {
        TODO("Not yet implemented")
    }

    override suspend fun getById(id: Long): PhotoPair? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllByProductId(productId: Long): List<PhotoPair> {
        TODO("Not yet implemented")
    }

}