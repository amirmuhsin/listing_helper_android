package com.amirmuhsin.listinghelper.ui.s2_product_detail

import androidx.lifecycle.viewModelScope
import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.domain.product.ProductRepository
import com.amirmuhsin.listinghelper.networking.model.product.ProductAM
import com.amirmuhsin.listinghelper.domain.model.PhotoPair
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
): BaseViewModel() {

    private val _fProduct: MutableStateFlow<ProductAM?> = MutableStateFlow(null)
    val fProduct = _fProduct.asStateFlow()

    private val _flCleanedPhotos: MutableStateFlow<List<PhotoPair>> = MutableStateFlow(emptyList())
    val flCleanedPhotos = _flCleanedPhotos.asStateFlow()

    fun onBarcodeChanged(sku: String) {
        viewModelScope.launch {
            val product = productRepository.getProductsBySku(sku)
            _fProduct.value = product
        }
    }

    fun setCleanedPhotos(cleanedPhotoPairs: List<PhotoPair>) {
        _flCleanedPhotos.value = cleanedPhotoPairs
    }

}