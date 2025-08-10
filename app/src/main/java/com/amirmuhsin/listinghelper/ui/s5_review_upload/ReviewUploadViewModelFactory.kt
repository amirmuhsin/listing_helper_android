package com.amirmuhsin.listinghelper.ui.s5_review_upload

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amirmuhsin.listinghelper.data.db.AppDatabase
import com.amirmuhsin.listinghelper.data.networking.ProductNetworkModule
import com.amirmuhsin.listinghelper.repository.PhotoPairLocalRepositoryImpl
import com.amirmuhsin.listinghelper.repository.ProductRemoteRepositoryImpl

class ReviewUploadViewModelFactory(
    private val context: Context
): ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReviewUploadViewModel::class.java)) {

            val db = AppDatabase.getInstance(context)

            @Suppress("UNCHECKED_CAST")
            return ReviewUploadViewModel(
                photoPairLocalRepository = PhotoPairLocalRepositoryImpl(db.photoPairDao()),
                productRemoteRepository = ProductRemoteRepositoryImpl(
                    context = context,
                    productService = ProductNetworkModule.productService,
                    imageService = ProductNetworkModule.imageService
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}