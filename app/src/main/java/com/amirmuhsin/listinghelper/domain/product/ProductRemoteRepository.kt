package com.amirmuhsin.listinghelper.domain.product

import android.net.Uri
import com.amirmuhsin.listinghelper.core_views.result.Result
import com.amirmuhsin.listinghelper.data.networking.model.product.ImageAM
import com.amirmuhsin.listinghelper.data.networking.model.product.ProductAM

interface ProductRemoteRepository {

    suspend fun getProductsBySku(sku: String): Result<ProductAM>

    suspend fun getProductById(itemId: Long): Result<ProductAM>

    suspend fun getImagesForItem(itemId: Long): Result<List<ImageAM>>

    suspend fun uploadImage(
        itemId: Long,
        photoId: String,
        uri: Uri,
        channelId: String
    ): Result<ImageAM>

}