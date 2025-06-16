package com.amirmuhsin.listinghelper.ui.s2_product_detail.list

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amirmuhsin.listinghelper.domain.model.PhotoPair

class CleanedPhotoAdapter(
    private val context: Context,
    private val onPhotoClick: (PhotoPair) -> Unit
): ListAdapter<PhotoPair, CleanedPhotoAdapter.CleanedPhotoViewHolder>(DIFF) {

    companion object {

        private val DIFF = object: DiffUtil.ItemCallback<PhotoPair>() {
            override fun areItemsTheSame(a: PhotoPair, b: PhotoPair) =
                a.internalId == b.internalId

            override fun areContentsTheSame(a: PhotoPair, b: PhotoPair) =
                a.cleanedUri == b.cleanedUri && a.bgCleanStatus == b.bgCleanStatus
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CleanedPhotoViewHolder {
        val layout = CleanedPhotoItemLayout(context, onPhotoClick)
        return CleanedPhotoViewHolder(layout)
    }

    override fun onBindViewHolder(holder: CleanedPhotoViewHolder, position: Int) {
        val pair = getItem(position)
        holder.layout.fillContent(pair)
    }

    inner class CleanedPhotoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val layout = itemView as CleanedPhotoItemLayout
    }
}
