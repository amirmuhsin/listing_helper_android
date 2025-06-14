package com.amirmuhsin.listinghelper.networking.model.response

import com.amirmuhsin.listinghelper.networking.model.product.ProductAM
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import java.io.Serial

data class PagedProductListResponse(
    @SerializedName("TotalItems") val totalItems: Int,
    @SerializedName("PageNumber") val pageNumber: Int,
    @SerializedName("PageSize") val pageSize: Int,
    @SerializedName("Items") val items: List<ProductAM>,
    @SerializedName("TotalPages") val totalPages: Int,
    @SerializedName("HasPreviousPage") val hasPreviousPage: Boolean,
    @SerializedName("HasNextPage") val hasNextPage: Boolean,
    @SerializedName("NextPageNumber") val nextPageNumber: Int,
    @SerializedName("PreviousPageNumber") val previousPageNumber: Int
)