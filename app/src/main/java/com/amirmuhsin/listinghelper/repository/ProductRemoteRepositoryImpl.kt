package com.amirmuhsin.listinghelper.repository

import android.content.Context
import android.net.Uri
import android.util.Base64
import com.amirmuhsin.listinghelper.core_views.result.Result
import com.amirmuhsin.listinghelper.core_views.result.ResultError
import com.amirmuhsin.listinghelper.data.networking.api.ImageService
import com.amirmuhsin.listinghelper.data.networking.api.ProductService
import com.amirmuhsin.listinghelper.data.networking.model.product.ImageAM
import com.amirmuhsin.listinghelper.data.networking.model.product.ProductAM
import com.amirmuhsin.listinghelper.data.networking.model.request.UploadProductImageRequest
import com.amirmuhsin.listinghelper.domain.product.ProductRemoteRepository

class ProductRemoteRepositoryImpl(
    private val context: Context,
    private val productService: ProductService,
    private val imageService: ImageService
): ProductRemoteRepository {

    override suspend fun getProductsBySku(sku: String): Result<ProductAM> = Result.runCatching {
        val resp = productService.getProductBySKU(sku)
        if (!resp.isSuccessful) throw retrofit2.HttpException(resp)

        val results = resp.body()?.items
            ?: throw IllegalStateException("Response body is null")

        println("Results: $results")

        results.firstOrNull { it.sku == sku }
            ?: throw NoSuchElementException("No product found with SKU: $sku")
    }

    override suspend fun getProductById(itemId: Long): Result<ProductAM> = Result.runCatching {
        val resp = productService.getProductById(itemId)
        if (!resp.isSuccessful) throw retrofit2.HttpException(resp)
        resp.body() ?: throw IllegalStateException("Response body is null")
    }

    override suspend fun getImagesForItem(itemId: Long): Result<List<ImageAM>> = Result.runCatching {
        val resp = imageService.getProductImages(itemId)
        if (!resp.isSuccessful) throw retrofit2.HttpException(resp)
        resp.body() ?: throw IllegalStateException("Response body is null")
    }

    override suspend fun uploadImage(
        itemId: Long,
        photoId: String,
        uri: Uri,
        channelId: String
    ): Result<ImageAM> = Result.runCatching {
        if (itemId == -1L) {
            throw IllegalArgumentException("Invalid itemId: $itemId")
        }

        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open URI: $uri")
        val bytes = inputStream.use { it.readBytes() }

        val base64Data = Base64.encodeToString(bytes, Base64.NO_WRAP)

        val request = UploadProductImageRequest(
            itemData = base64Data,
            fileName = "${itemId}_${photoId}.jpg",
            salesChannelId = channelId
        )

        val response = imageService.uploadProductImage(itemId, request)
        if (!response.isSuccessful) {
            throw retrofit2.HttpException(response)
        }
        response.body() ?: throw IllegalStateException("Response body is null")
    }
}
