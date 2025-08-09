package com.amirmuhsin.listinghelper.ui.s2_0_product_detail

import androidx.lifecycle.viewModelScope
import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.data.networking.model.product.ProductAM
import com.amirmuhsin.listinghelper.domain.photo.AddPhotoItemButton
import com.amirmuhsin.listinghelper.domain.photo.PhotoItem
import com.amirmuhsin.listinghelper.domain.photo.PhotoPair
import com.amirmuhsin.listinghelper.domain.product.ProductRemoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val productRemoteRepository: ProductRemoteRepository,
): BaseViewModel() {

    private val _fProduct: MutableStateFlow<ProductAM?> = MutableStateFlow(null)
    val fProduct = _fProduct.asStateFlow()

    private val _flCleanedPhotos: MutableStateFlow<List<PhotoItem>> = MutableStateFlow(listOf(AddPhotoItemButton))
    val flCleanedPhotos = _flCleanedPhotos.asStateFlow()

    fun onBarcodeChanged(sku: String) {
        viewModelScope.launch {
            val product = productRemoteRepository.getProductsBySku(sku)
            _fProduct.value = product
        }
    }

    fun setCleanedPhotos(cleanedPhotoPairs: List<PhotoItem>) {
        val cleanedPhotos = cleanedPhotoPairs.filterNot { it is AddPhotoItemButton }
        // add AddPhotoItemButton at the end of the list
        val updatedList = cleanedPhotos.toMutableList().apply {
            if (isEmpty() || last() !is AddPhotoItemButton) {
                add(AddPhotoItemButton)
            }
        }
        _flCleanedPhotos.value = updatedList
    }

    fun appendCleanedPhotos(newPhotos: List<PhotoPair>) {
        val existingPairs = _flCleanedPhotos.value.filterIsInstance<PhotoPair>()
        val updatedPhotos = newPhotos.mapIndexed { index, photo ->
            photo.copy(order = existingPairs.size + index + 1)
        }
        val updatedList = (existingPairs + updatedPhotos).toMutableList<PhotoItem>().apply {
            add(AddPhotoItemButton)
        }
        _flCleanedPhotos.value = updatedList
    }
}