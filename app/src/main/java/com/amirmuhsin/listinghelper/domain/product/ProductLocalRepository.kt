package com.amirmuhsin.listinghelper.domain.product

interface ProductLocalRepository {

    suspend fun create(product: Product): Long
    suspend fun update(product: Product)
    suspend fun delete(product: Product)

    suspend fun getById(id: Long): Product?
    suspend fun getAll(): List<Product>
    suspend fun updateStatus(productId: Long, status: Product.Status)
    suspend fun updateImageCount(productId: Long, count: Int)

}