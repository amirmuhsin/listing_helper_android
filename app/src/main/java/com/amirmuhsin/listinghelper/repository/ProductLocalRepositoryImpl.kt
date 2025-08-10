package com.amirmuhsin.listinghelper.repository

import com.amirmuhsin.listinghelper.data.db.dao.ProductDao
import com.amirmuhsin.listinghelper.data.db.mapper.toDomain
import com.amirmuhsin.listinghelper.data.db.mapper.toEntity
import com.amirmuhsin.listinghelper.domain.product.Product
import com.amirmuhsin.listinghelper.domain.product.ProductLocalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductLocalRepositoryImpl(
    private val productDao: ProductDao
): ProductLocalRepository {

    override suspend fun create(product: Product): Long = withContext(Dispatchers.IO) {
        val productEntity = product.toEntity()
        val createdProductId = productDao.upsert(productEntity)
        createdProductId
    }

    override suspend fun update(product: Product) = withContext(Dispatchers.IO) {
        val productEntity = product.toEntity()
        productDao.upsert(productEntity)
        Unit
    }

    override suspend fun delete(product: Product) = withContext(Dispatchers.IO) {
        productDao.delete(product.id)
    }

    override suspend fun getById(id: Long): Product? = withContext(Dispatchers.IO) {
        val entity = productDao.getById(id)
        entity?.toDomain()
    }

    override suspend fun getAll(): List<Product> = withContext(Dispatchers.IO) {
        productDao.getAll().map { it.toDomain() }
    }

    override suspend fun updateStatus(productId: Long, status: Product.Status) = withContext(Dispatchers.IO) {
        productDao.updateStatus(productId, status.name)
    }

    override suspend fun updateImageCount(productId: Long, count: Int) = withContext(Dispatchers.IO) {
        productDao.updateImageCount(productId, count)
    }
}