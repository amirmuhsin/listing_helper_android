package com.amirmuhsin.listinghelper.ui.s1_home

import androidx.lifecycle.viewModelScope
import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.domain.product.DateHeaderItem
import com.amirmuhsin.listinghelper.domain.product.Product
import com.amirmuhsin.listinghelper.domain.product.ProductItem
import com.amirmuhsin.listinghelper.domain.product.ProductListItem
import com.amirmuhsin.listinghelper.domain.product.ProductLocalRepository
import com.amirmuhsin.listinghelper.ui.s1_home.command.HomeCommands
import com.amirmuhsin.listinghelper.util.Time
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val productLocalRepository: ProductLocalRepository
): BaseViewModel() {

    private val _fProductItems = MutableStateFlow<List<ProductListItem>>(emptyList())
    val fProductItems = _fProductItems

    private var allProducts: List<Product> = emptyList()

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
            allProducts = products
            _fProductItems.value = groupProductsByDate(products)
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            val filteredProducts = if (query.isBlank()) {
                allProducts
            } else {
                allProducts.filter { product ->
                    product.name.contains(query, ignoreCase = true) ||
                    product.sku.contains(query, ignoreCase = true)
                }
            }
            _fProductItems.value = groupProductsByDate(filteredProducts)
        }
    }

    private fun groupProductsByDate(products: List<Product>): List<ProductListItem> {
        val items = mutableListOf<ProductListItem>()
        var lastDate: String? = null

        products.forEach { product ->
            val dateString = Time.isoUtcToDateOnly(product.addedTime)

            if (dateString != lastDate) {
                items.add(DateHeaderItem(dateString))
                lastDate = dateString
            }

            items.add(ProductItem(product))
        }

        return items
    }
}