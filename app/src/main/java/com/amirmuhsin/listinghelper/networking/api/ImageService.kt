package com.amirmuhsin.listinghelper.networking.api

import com.amirmuhsin.listinghelper.networking.model.product.ImageModelApi
import com.amirmuhsin.listinghelper.networking.model.request.UploadProductImageRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ImageService {

    @GET("v1/items/{itemId}/images")
    suspend fun getProductImages(
        @Path("itemId") itemId: Long
    ): Response<List<ImageModelApi>>

    @GET("v1/items/imagedata/{imageId}")
    suspend fun getProductImageData(
        @Path("imageId") imageId: Long
    ): Response<ImageModelApi>

    @GET("v1/items/{itemId}/images/{imageId}")
    suspend fun getProductImage(
        @Path("itemId") itemId: Long,
        @Path("imageId") imageId: Long
    ): Response<ImageModelApi>

    @POST("v1/items/{itemId}/images")
    suspend fun uploadProductImage(
        @Path("itemId") itemId: Long,
        @Body request: UploadProductImageRequest
    ): Response<ImageModelApi>

}