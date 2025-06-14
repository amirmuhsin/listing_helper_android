package com.amirmuhsin.listinghelper.domain.product

import com.amirmuhsin.listinghelper.networking.model.product.ImageAM
import com.amirmuhsin.listinghelper.networking.model.product.ProductAM
import com.amirmuhsin.listinghelper.networking.model.request.UploadProductImageRequest

interface ProductRepository {

    suspend fun getProductsBySku(sku: String): ProductAM

    suspend fun getProductById(itemId: Long): ProductAM

    suspend fun getImagesForItem(itemId: Long): List<ImageAM>

    suspend fun uploadImage(
        itemId: Long,
        request: UploadProductImageRequest
    ): ImageAM

}