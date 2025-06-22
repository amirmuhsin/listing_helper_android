package com.amirmuhsin.listinghelper.data.networking.api

import com.amirmuhsin.listinghelper.data.networking.model.product.ProductAM
import com.amirmuhsin.listinghelper.data.networking.model.response.PagedProductListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {

    @GET("v1/items")
    suspend fun getProductBySKU(
        @Query("searchKeyWord") searchKeyWord: String
    ): Response<PagedProductListResponse>

    @GET("v1/items/{itemId}")
    suspend fun getProductById(
        @Path("itemId") itemId: Long
    ): Response<ProductAM>

}