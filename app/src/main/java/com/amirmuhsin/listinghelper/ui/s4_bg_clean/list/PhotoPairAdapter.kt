package com.amirmuhsin.listinghelper.ui.s4_bg_clean.list

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class PhotoPairAdapter(
    val context: Context,
    val onPhotoClick: () -> Unit,
    val onPhotoRemoveClick: (uri: Uri) -> Unit,
): ListAdapter<PhotoPair, PhotoPairAdapter.PhotoPairViewHolder>(DIFF) {

    companion object {

        private val DIFF = object: DiffUtil.ItemCallback<PhotoPair>() {
            override fun areItemsTheSame(a: PhotoPair, b: PhotoPair) = a.id == b.id
            override fun areContentsTheSame(a: PhotoPair, b: PhotoPair) =
                a.status == b.status && a.cleanedUri == b.cleanedUri
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoPairViewHolder {
        val layout = PhotoPairItemLayout(context, onPhotoClick, onPhotoRemoveClick)

        return PhotoPairViewHolder(layout)
    }

    override fun onBindViewHolder(holder: PhotoPairViewHolder, position: Int) {
        val pair = getItem(position)
        holder.layout.fillContent(pair)
    }

    inner class PhotoPairViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val layout = itemView as PhotoPairItemLayout
    }

}