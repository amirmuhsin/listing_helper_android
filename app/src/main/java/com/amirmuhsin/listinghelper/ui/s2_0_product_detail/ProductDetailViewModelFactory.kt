package com.amirmuhsin.listinghelper.ui.s2_0_product_detail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amirmuhsin.listinghelper.data.db.AppDatabase
import com.amirmuhsin.listinghelper.data.networking.ProductNetworkModule
import com.amirmuhsin.listinghelper.repository.PhotoPairLocalRepositoryImpl
import com.amirmuhsin.listinghelper.repository.ProductLocalRepositoryImpl
import com.amirmuhsin.listinghelper.repository.ProductRemoteRepositoryImpl

class ProductDetailViewModelFactory(
    private val appContext: Context
): ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductDetailViewModel::class.java)) {

            val appDatabase = AppDatabase.getInstance(appContext)

            @Suppress("UNCHECKED_CAST")
            return ProductDetailViewModel(
                productLocalRepository = ProductLocalRepositoryImpl(appDatabase.productDao()),
                photoPairLocalRepository = PhotoPairLocalRepositoryImpl(appDatabase.productDao(), appDatabase.photoPairDao()),
                productRemoteRepository = ProductRemoteRepositoryImpl(
                    context = appContext,
                    ProductNetworkModule.productService,
                    ProductNetworkModule.imageService
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
