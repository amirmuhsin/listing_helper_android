package com.amirmuhsin.listinghelper.ui.s1_home.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amirmuhsin.listinghelper.domain.product.Product

class ProductListAdapter(
    private val onProductClick: (Product) -> Unit,
): ListAdapter<Product, ProductListAdapter.ProductListItemViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListItemViewHolder {
        val layout = ProductItemLayout(parent.context, onProductClick)
        return ProductListItemViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ProductListItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.layout.fillContent(item)
    }

    inner class ProductListItemViewHolder(item: View): RecyclerView.ViewHolder(item) {
        val layout = item as ProductItemLayout
    }
}