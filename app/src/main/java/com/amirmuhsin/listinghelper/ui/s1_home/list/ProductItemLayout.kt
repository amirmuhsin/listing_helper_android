package com.amirmuhsin.listinghelper.ui.s1_home.list

import android.content.Context
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.databinding.ItemLayoutProductBinding
import com.amirmuhsin.listinghelper.domain.product.Product
import com.amirmuhsin.listinghelper.util.Time

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
        binding.tvSku.text = if (product.sku.isNotBlank()) "SKU: ${product.sku}" else "SKU: —"
        binding.tvShortDescription.text = product.shortDescription

        binding.tvChipStatus.text = when (product.status) {
            Product.Status.DRAFT -> "Draft"
            Product.Status.DONE -> "Uploaded"
            Product.Status.HAS_FAILURE -> "Failed"
        }

        when (product.status) {
            Product.Status.DRAFT -> {
                binding.ivStatus.setImageResource(R.drawable.ic_draft)
            }

            Product.Status.DONE -> {
                binding.ivStatus.setImageResource(R.drawable.ic_check)
            }

            Product.Status.HAS_FAILURE -> {
                binding.ivStatus.setImageResource(R.drawable.ic_warning)
            }
        }

        binding.tvBadgePhotoCount.text = product.totalImageCount.toString() + " photos"

        binding.tvDate.text = "Created • " + Time.isoUtcToDeviceText(product.addedTime)

    }

}