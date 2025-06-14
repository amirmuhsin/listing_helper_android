package com.amirmuhsin.listinghelper.networking.model.response

import com.amirmuhsin.listinghelper.networking.model.product.ProductModelApi
import kotlinx.serialization.SerialName

data class PagedProductListResponse(
    @SerialName("TotalItems") val totalItems: Int,
    @SerialName("PageNumber") val pageNumber: Int,
    @SerialName("PageSize") val pageSize: Int,
    @SerialName("Items") val items: List<ProductModelApi>,
    @SerialName("TotalPages") val totalPages: Int,
    @SerialName("HasPreviousPage") val hasPreviousPage: Boolean,
    @SerialName("HasNextPage") val hasNextPage: Boolean,
    @SerialName("NextPageNumber") val nextPageNumber: Int,
    @SerialName("PreviousPageNumber") val previousPageNumber: Int
)