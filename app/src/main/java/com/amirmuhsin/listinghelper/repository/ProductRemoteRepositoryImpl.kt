package com.amirmuhsin.listinghelper.repository

import android.content.Context
import android.net.Uri
import android.util.Base64
import com.amirmuhsin.listinghelper.domain.product.ProductRemoteRepository
import com.amirmuhsin.listinghelper.data.networking.api.ImageService
import com.amirmuhsin.listinghelper.data.networking.api.ProductService
import com.amirmuhsin.listinghelper.data.networking.model.product.ImageAM
import com.amirmuhsin.listinghelper.data.networking.model.product.ProductAM
import com.amirmuhsin.listinghelper.data.networking.model.request.UploadProductImageRequest
import retrofit2.HttpException

class ProductRemoteRepositoryImpl(
    private val context: Context,
    private val productService: ProductService,
    private val imageService: ImageService
): ProductRemoteRepository {

    override suspend fun getProductsBySku(sku: String): ProductAM {
        val resp = productService.getProductBySKU(sku)
        if (!resp.isSuccessful) throw HttpException(resp)
        // you might want to null-check body/data more carefully

        val results = resp.body()?.items

        println("Results: $results")

        results?.forEach { product ->
            if (product.sku == sku) {
                return product
            }
        }
        throw NoSuchElementException("No product found with SKU: $sku")
    }

    override suspend fun getProductById(itemId: Long): ProductAM {
        val resp = productService.getProductById(itemId)
        if (!resp.isSuccessful) throw HttpException(resp)
        return resp.body()!!
    }

    override suspend fun getImagesForItem(itemId: Long): List<ImageAM> {
        val resp = imageService.getProductImages(itemId)
        if (!resp.isSuccessful) throw HttpException(resp)
        return resp.body()!!
    }

    override suspend fun uploadImage(itemId: Long, photoId: String, uri: Uri, channelId: String): ImageAM {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open URI: $uri")
        val bytes = inputStream.use { it.readBytes() }

        val base64Data = Base64.encodeToString(bytes, Base64.NO_WRAP)

        val request = UploadProductImageRequest(
            itemData = base64Data,
            fileName = "${itemId}_${photoId}.jpg", // or derive a better name
            salesChannelId = channelId
        )

        val response = imageService.uploadProductImage(itemId, request)
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        return response.body()!!
    }
}
