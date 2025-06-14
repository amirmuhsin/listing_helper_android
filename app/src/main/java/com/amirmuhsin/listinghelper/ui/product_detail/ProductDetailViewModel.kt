package com.amirmuhsin.listinghelper.ui.product_detail

import androidx.lifecycle.viewModelScope
import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.domain.product.ProductRepository
import com.amirmuhsin.listinghelper.networking.model.product.ProductAM
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel(
    private val productRepository: ProductRepository,
): BaseViewModel() {

    private val _fProduct: MutableStateFlow<ProductAM?> = MutableStateFlow(null)
    val fProduct = _fProduct.asStateFlow()

    fun onBarcodeChanged(sku: String) {
        viewModelScope.launch {
            val product = productRepository.getProductsBySku("R06-685510")
            _fProduct.value = product
        }
    }



}