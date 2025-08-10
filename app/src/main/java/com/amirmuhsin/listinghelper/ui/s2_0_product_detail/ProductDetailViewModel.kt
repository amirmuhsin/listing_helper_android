package com.amirmuhsin.listinghelper.ui.s2_0_product_detail

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.domain.photo.AddPhotoItemButton
import com.amirmuhsin.listinghelper.domain.photo.PhotoItem
import com.amirmuhsin.listinghelper.domain.photo.PhotoPair
import com.amirmuhsin.listinghelper.domain.photo.PhotoPairLocalRepository
import com.amirmuhsin.listinghelper.domain.product.Product
import com.amirmuhsin.listinghelper.domain.product.ProductLocalRepository
import com.amirmuhsin.listinghelper.domain.product.ProductRemoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ProductDetailViewModel(
    private val productLocalRepository: ProductLocalRepository,
    private val photoPairLocalRepository: PhotoPairLocalRepository,
    private val productRemoteRepository: ProductRemoteRepository,
): BaseViewModel() {

    private val _fProduct: MutableStateFlow<Product?> = MutableStateFlow(null)
    val fProduct = _fProduct.asStateFlow()

    private val _flPhotoPairs: MutableStateFlow<List<PhotoItem>> = MutableStateFlow(listOf(AddPhotoItemButton))
    val flPhotoPairs = _flPhotoPairs.asStateFlow()

    private var productId: Long = -1L

    fun fetchAndUpdateBySku(sku: String) {
        viewModelScope.launch {
            val productAM = productRemoteRepository.getProductsBySku(sku)
            val product = _fProduct.value

            // update product model with productAM and save locally
            val updatedProduct = product?.copy(
                sku = productAM.sku,
                isActive = productAM.isActive,
                name = productAM.name,
                description = productAM.description,
                shortDescription = productAM.shortDescription,
            ) ?: run {
                showErrorSnackbar("Product not found")
                return@launch
            }

            productLocalRepository.update(updatedProduct)
            _fProduct.value = updatedProduct
        }
    }

    fun getLocalProductByIdInFull(productId: Long) {
        this.productId = productId
        viewModelScope.launch {
            notifyProduct()
            notifyPhotoPairs()
        }
    }

    fun addNewCleanedPhotos(uris: List<Uri>) {
        viewModelScope.launch {
            val base = _flPhotoPairs.value.count { it is PhotoPair }
            uris.forEachIndexed { i, uri ->
                photoPairLocalRepository.create(
                    PhotoPair(
                        internalId = UUID.randomUUID().toString(),
                        productId = productId,
                        originalUri = uri,
                        cleanedUri = uri,
                        bgCleanStatus = PhotoPair.BgCleanStatus.COMPLETED,
                        order = base + i,
                        uploadStatus = PhotoPair.UploadStatus.PENDING
                    )
                )
            }
            notifyPhotoPairs()

            val product = _fProduct.value ?: Product.createEmpty()
            _fProduct.value = product.copy(
                status = Product.Status.IN_PROGRESS,
                totalImageCount = _flPhotoPairs.value.count { it is PhotoPair })
            productLocalRepository.update(_fProduct.value!!)
        }
    }

    private suspend fun notifyProduct() {
        val product = productLocalRepository.getById(productId)
        _fProduct.value = product
    }

    private suspend fun notifyPhotoPairs() {
        val photoPairs = photoPairLocalRepository.getAllByProductId(productId)
        _flPhotoPairs.value = photoPairs + AddPhotoItemButton
    }
}