package com.amirmuhsin.listinghelper.ui.common.fullscreen_image

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amirmuhsin.listinghelper.data.db.AppDatabase
import com.amirmuhsin.listinghelper.repository.PhotoPairLocalRepositoryImpl
import com.amirmuhsin.listinghelper.repository.ProductLocalRepositoryImpl

class FullScreenViewerViewModelFactory(
    private val appContext: Context,
    private val productId: Long,
    private val startPhotoPairId: String
): ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FullScreenViewerViewModel::class.java)) {
            val db = AppDatabase.getInstance(appContext)

            @Suppress("UNCHECKED_CAST")
            return FullScreenViewerViewModel(
                appContext = appContext,
                productId = productId,
                startPhotoPairId = startPhotoPairId,
                productLocalRepository = ProductLocalRepositoryImpl(db.productDao()),
                photoPairLocalRepository = PhotoPairLocalRepositoryImpl(db.photoPairDao())
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}
