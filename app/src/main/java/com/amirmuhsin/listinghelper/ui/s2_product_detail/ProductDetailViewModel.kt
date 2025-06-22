package com.amirmuhsin.listinghelper.ui.s2_product_detail

import androidx.lifecycle.viewModelScope
import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.domain.model.AddPhotoItemButton
import com.amirmuhsin.listinghelper.domain.model.PhotoItem
import com.amirmuhsin.listinghelper.domain.product.ProductRepository
import com.amirmuhsin.listinghelper.data.networking.model.product.ProductAM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
): BaseViewModel() {

    private val _fProduct: MutableStateFlow<ProductAM?> = MutableStateFlow(null)
    val fProduct = _fProduct.asStateFlow()

    private val _flCleanedPhotos: MutableStateFlow<List<PhotoItem>> = MutableStateFlow(listOf(AddPhotoItemButton))
    val flCleanedPhotos = _flCleanedPhotos.asStateFlow()

    fun onBarcodeChanged(sku: String) {
        viewModelScope.launch {
            val product = productRepository.getProductsBySku(sku)
            _fProduct.value = product
        }
    }

    fun setCleanedPhotos(cleanedPhotoPairs: List<PhotoItem>) {
        // check if the list contains AddPhotoItemButton, and if so, remove it
        val cleanedPhotos = cleanedPhotoPairs.filterNot { it is AddPhotoItemButton }
        // add AddPhotoItemButton at the end of the list
        val updatedList = cleanedPhotos.toMutableList().apply {
            if (isEmpty() || last() !is AddPhotoItemButton) {
                add(AddPhotoItemButton)
            }
        }
        _flCleanedPhotos.value = updatedList
    }

}