package com.amirmuhsin.listinghelper.ui.common.main

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.domain.photo.PhotoPair
import com.amirmuhsin.listinghelper.domain.photo.PhotoPairLocalRepository
import com.amirmuhsin.listinghelper.domain.product.Product
import com.amirmuhsin.listinghelper.domain.product.ProductLocalRepository
import com.amirmuhsin.listinghelper.ui.common.main.command.MainCommands
import kotlinx.coroutines.launch
import java.util.UUID

class MainViewModel(
    private val productRepo: ProductLocalRepository,
    private val photoRepo: PhotoPairLocalRepository
): BaseViewModel() {

    fun createNewProductWithPhotos(uris: List<Uri>) {
        viewModelScope.launch {
            val productId = productRepo.create(Product.createEmpty(uris.size))

            uris.forEachIndexed { index, uri ->
                val item = PhotoPair(
                    internalId = UUID.randomUUID().toString(),
                    productId = productId,
                    originalUri = uri,
                    cleanedUri = uri,
                    bgCleanStatus = PhotoPair.BgCleanStatus.COMPLETED,
                    order = index + 1,
                    uploadStatus = PhotoPair.UploadStatus.PENDING
                )
                photoRepo.create(item)
            }

            sendCommand(MainCommands.NewProductWithImagesCreated(productId))
        }
    }
}