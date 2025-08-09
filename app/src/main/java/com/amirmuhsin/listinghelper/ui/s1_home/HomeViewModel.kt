package com.amirmuhsin.listinghelper.ui.s1_home

import androidx.lifecycle.viewModelScope
import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.domain.product.Product
import com.amirmuhsin.listinghelper.domain.product.ProductLocalRepository
import com.amirmuhsin.listinghelper.ui.s1_home.command.HomeCommands
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val productLocalRepository: ProductLocalRepository
): BaseViewModel() {

    private val _fProducts = MutableStateFlow<List<Product>>(emptyList())
    val fProducts = _fProducts

    fun createNewProduct() {
        viewModelScope.launch {
            val newProduct = Product.createEmpty()
            val createdProductId = productLocalRepository.create(newProduct)

            sendCommand(HomeCommands.NewProductCreated(createdProductId))
        }
    }

    fun getAllProducts() {
        viewModelScope.launch {
            val products = productLocalRepository.getAll()
            // Handle the list of products as needed, e.g., update UI or state
        }
    }
}