package com.amirmuhsin.listinghelper.ui.photo_capture.list

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amirmuhsin.listinghelper.ui.product_detail.list.PhotoItemLayout

class ThumbsAdapter(val context: Context,
    val onPhotoClick: () -> Unit,
    val onPhotoRemoveClick: (uri: Uri) -> Unit,
    ) : RecyclerView.Adapter<ThumbsAdapter.PhotoViewHolder>() {

    private val list = mutableListOf<Uri>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val layout = PhotoItemLayout(context, onPhotoClick, onPhotoRemoveClick)
        return PhotoViewHolder(layout)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val uri = list[position]
        holder.layout.fillContent(uri)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addNewPhoto(uri: Uri) {
        list.add(0, uri)
        notifyItemInserted(0)
    }

    fun removePhoto(uri: Uri) {
        val indexOf = list.indexOf(uri)
        list.removeAt(indexOf)
        notifyItemRemoved(indexOf)
    }

    inner class PhotoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val layout = itemView as PhotoItemLayout
    }

}