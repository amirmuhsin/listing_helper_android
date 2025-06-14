package com.amirmuhsin.listinghelper.networking.api

import com.amirmuhsin.listinghelper.networking.model.product.ProductModelApi
import com.amirmuhsin.listinghelper.networking.model.response.PagedProductListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductService {

    // v1/items?searchKeyWord=R06-685510
    // get items by SKU
    @GET("v1/items")
    suspend fun getProductBySKU(
        @Query("searchKeyWord") searchKeyWord: String
    ): Response<PagedProductListResponse>

    // get item by itemID
    @GET("v1/items/{itemID}")
    suspend fun getProductById(
    ): Response<ProductModelApi>

}