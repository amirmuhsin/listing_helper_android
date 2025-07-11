package com.amirmuhsin.listinghelper.domain.product

import android.net.Uri
import com.amirmuhsin.listinghelper.data.networking.model.product.ImageAM
import com.amirmuhsin.listinghelper.data.networking.model.product.ProductAM

interface ProductRepository {

    suspend fun getProductsBySku(sku: String): ProductAM

    suspend fun getProductById(itemId: Long): ProductAM

    suspend fun getImagesForItem(itemId: Long): List<ImageAM>

    suspend fun uploadImage(
        itemId: Long,
        uri: Uri,
        channelId: String
    ): ImageAM

}