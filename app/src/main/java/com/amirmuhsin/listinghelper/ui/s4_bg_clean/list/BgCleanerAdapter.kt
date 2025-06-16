package com.amirmuhsin.listinghelper.ui.s4_bg_clean.list

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amirmuhsin.listinghelper.domain.model.PhotoPair

class BgCleanerAdapter(
    val context: Context,
    val onPhotoClick: () -> Unit,
    val onPhotoRemoveClick: (uri: Uri) -> Unit,
): ListAdapter<PhotoPair, BgCleanerAdapter.PhotoPairViewHolder>(DIFF) {

    companion object {

        private val DIFF = object: DiffUtil.ItemCallback<PhotoPair>() {
            override fun areItemsTheSame(a: PhotoPair, b: PhotoPair) = a.internalId == b.internalId
            override fun areContentsTheSame(a: PhotoPair, b: PhotoPair) =
                a.bgCleanStatus == b.bgCleanStatus && a.cleanedUri == b.cleanedUri
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoPairViewHolder {
        val layout = BgCleanerItemLayout(context, onPhotoClick, onPhotoRemoveClick)
        return PhotoPairViewHolder(layout)
    }

    override fun onBindViewHolder(holder: PhotoPairViewHolder, position: Int) {
        val pair = getItem(position)
        holder.layout.fillContent(pair)
    }

    inner class PhotoPairViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val layout = itemView as BgCleanerItemLayout
    }

}