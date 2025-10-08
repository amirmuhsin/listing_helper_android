package com.amirmuhsin.listinghelper.domain.product

sealed class ProductListItem

data class ProductItem(val product: Product) : ProductListItem()

data class DateHeaderItem(val date: String) : ProductListItem()

data class EmptyStateItem(val message: String) : ProductListItem()
