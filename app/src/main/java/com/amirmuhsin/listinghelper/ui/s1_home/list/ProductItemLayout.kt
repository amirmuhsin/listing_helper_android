package com.amirmuhsin.listinghelper.ui.s1_home.list

import android.content.Context
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.amirmuhsin.listinghelper.databinding.ItemLayoutProductBinding
import com.amirmuhsin.listinghelper.domain.product.Product

class ProductItemLayout(
    context: Context,
    private val onProductClick: (Product) -> Unit,
): RelativeLayout(context) {

    private val binding = ItemLayoutProductBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    private var product: Product? = null

    init {
        binding.clRoot.setOnClickListener {
            product?.let { onProductClick(it) }
        }
    }

    fun fillContent(product: Product) {
        this.product = product

        binding.tvName.text = product.name
    }

}