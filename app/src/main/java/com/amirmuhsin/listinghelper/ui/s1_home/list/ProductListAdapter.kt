package com.amirmuhsin.listinghelper.ui.s1_home.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amirmuhsin.listinghelper.domain.product.DateHeaderItem
import com.amirmuhsin.listinghelper.domain.product.EmptyStateItem
import com.amirmuhsin.listinghelper.domain.product.Product
import com.amirmuhsin.listinghelper.domain.product.ProductItem
import com.amirmuhsin.listinghelper.domain.product.ProductListItem

class ProductListAdapter(
    private val onProductClick: (Product) -> Unit,
): ListAdapter<ProductListItem, RecyclerView.ViewHolder>(DIFF) {

    companion object {
        private const val TYPE_DATE_HEADER = 0
        private const val TYPE_PRODUCT = 1
        private const val TYPE_EMPTY_STATE = 2

        private val DIFF = object : DiffUtil.ItemCallback<ProductListItem>() {
            override fun areItemsTheSame(oldItem: ProductListItem, newItem: ProductListItem): Boolean {
                return when {
                    oldItem is ProductItem && newItem is ProductItem ->
                        oldItem.product.id == newItem.product.id
                    oldItem is DateHeaderItem && newItem is DateHeaderItem ->
                        oldItem.date == newItem.date
                    oldItem is EmptyStateItem && newItem is EmptyStateItem ->
                        true
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: ProductListItem, newItem: ProductListItem): Boolean {
                return when {
                    oldItem is ProductItem && newItem is ProductItem ->
                        oldItem.product == newItem.product
                    oldItem is DateHeaderItem && newItem is DateHeaderItem ->
                        oldItem.date == newItem.date
                    oldItem is EmptyStateItem && newItem is EmptyStateItem ->
                        oldItem.message == newItem.message
                    else -> false
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DateHeaderItem -> TYPE_DATE_HEADER
            is ProductItem -> TYPE_PRODUCT
            is EmptyStateItem -> TYPE_EMPTY_STATE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_DATE_HEADER -> {
                val layout = DateHeaderItemLayout(parent.context)
                DateHeaderViewHolder(layout)
            }
            TYPE_PRODUCT -> {
                val layout = ProductItemLayout(parent.context, onProductClick)
                ProductViewHolder(layout)
            }
            TYPE_EMPTY_STATE -> {
                val layout = EmptyStateItemLayout(parent.context)
                EmptyStateViewHolder(layout)
            }
            else -> error("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is DateHeaderItem -> {
                (holder as DateHeaderViewHolder).layout.fillContent(item.date)
            }
            is ProductItem -> {
                (holder as ProductViewHolder).layout.fillContent(item.product)
            }
            is EmptyStateItem -> {
                (holder as EmptyStateViewHolder).layout.fillContent(item.message)
            }
        }
    }

    inner class DateHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layout = itemView as DateHeaderItemLayout
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layout = itemView as ProductItemLayout
    }

    inner class EmptyStateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layout = itemView as EmptyStateItemLayout
    }
}