package com.amirmuhsin.listinghelper.ui.s5_review_upload.list

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amirmuhsin.listinghelper.domain.model.PhotoPair

class ConfirmationAdapter(
    private val context: Context,
    private val onPhotoClick: (PhotoPair) -> Unit,
    private val onPhotoRemove: (PhotoPair) -> Unit
): ListAdapter<PhotoPair, ConfirmationAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object: DiffUtil.ItemCallback<PhotoPair>() {
            override fun areItemsTheSame(a: PhotoPair, b: PhotoPair) = a.internalId == b.internalId
            override fun areContentsTheSame(a: PhotoPair, b: PhotoPair) =
                a.order == b.order && a.cleanedUri == b.cleanedUri && a.uploadStatus == b.uploadStatus
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val layout = ConfirmationItemLayout(context, onPhotoClick, onPhotoRemove)
        return VH(layout)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val pair = getItem(position)
        holder.layout.fillContent(pair)
    }

    inner class VH(itemView: View): RecyclerView.ViewHolder(itemView) {
        val layout = itemView as ConfirmationItemLayout
    }
}