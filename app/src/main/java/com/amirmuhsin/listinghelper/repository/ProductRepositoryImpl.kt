package com.amirmuhsin.listinghelper.repository

import com.amirmuhsin.listinghelper.domain.product.ProductRepository
import com.amirmuhsin.listinghelper.networking.api.ImageService
import com.amirmuhsin.listinghelper.networking.api.ProductService
import com.amirmuhsin.listinghelper.networking.model.product.ImageAM
import com.amirmuhsin.listinghelper.networking.model.product.ProductAM
import com.amirmuhsin.listinghelper.networking.model.request.UploadProductImageRequest
import retrofit2.HttpException

class ProductRepositoryImpl(
    private val productService: ProductService,
    private val imageService: ImageService
): ProductRepository {

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

    override suspend fun uploadImage(
        itemId: Long,
        request: UploadProductImageRequest
    ): ImageAM {
        val resp = imageService.uploadProductImage(itemId, request)
        if (!resp.isSuccessful) throw HttpException(resp)
        return resp.body()!!
    }
}
