package com.amirmuhsin.listinghelper.ui.s2_product_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amirmuhsin.listinghelper.networking.ProductNetworkModule
import com.amirmuhsin.listinghelper.repository.ProductRepositoryImpl

class ProductDetailViewModelFactory(
): ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductDetailViewModel(
                productRepository = ProductRepositoryImpl(
                    ProductNetworkModule.productService,
                    ProductNetworkModule.imageService
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
