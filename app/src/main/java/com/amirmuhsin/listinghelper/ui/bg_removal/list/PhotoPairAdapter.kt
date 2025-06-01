package com.amirmuhsin.listinghelper.ui.bg_removal.list

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PhotoPairAdapter(
    val context: Context,
    val onPhotoClick: () -> Unit,
    val onPhotoRemoveClick: (uri: Uri) -> Unit,
): RecyclerView.Adapter<PhotoPairAdapter.PhotoPairViewHolder>() {

    val pairs = mutableListOf<PhotoPair>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoPairViewHolder {
        val layout = PhotoPairItemLayout(context, onPhotoClick, onPhotoRemoveClick)

        return PhotoPairViewHolder(layout)
    }

    override fun onBindViewHolder(holder: PhotoPairViewHolder, position: Int) {
        holder.layout.fillContent(pairs[position])
    }

    override fun getItemCount(): Int {
        return pairs.size
    }

    fun setData(pairs: List<PhotoPair>) {
        this.pairs.clear()
        this.pairs.addAll(pairs)
        notifyDataSetChanged()
    }

    fun update(updatedPair: Pair<Int, PhotoPair?>) {
        val index = updatedPair.first
        val pair = updatedPair.second
        if (pair != null) {
            pairs[index] = pair
            notifyItemChanged(index)
        }
    }

    inner class PhotoPairViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val layout = itemView as PhotoPairItemLayout
    }

}