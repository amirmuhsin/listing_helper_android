package com.amirmuhsin.listinghelper.ui.s1_home.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amirmuhsin.listinghelper.domain.product.Product

class ProductListAdapter(
    private val onProductClick: (Product) -> Unit,
): RecyclerView.Adapter<ProductListAdapter.ProductListItemViewHolder>() {

    private val products: MutableList<Product> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListItemViewHolder {
        val layout = ProductItemLayout(parent.context, onProductClick)
        return ProductListItemViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ProductListItemViewHolder, position: Int) {
        val item = products[position]
        holder.layout.fillContent(item)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun submitList(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    inner class ProductListItemViewHolder(item: View): RecyclerView.ViewHolder(item) {

        val layout = item as ProductItemLayout
    }
}