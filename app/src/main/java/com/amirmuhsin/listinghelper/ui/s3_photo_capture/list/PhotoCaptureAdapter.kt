package com.amirmuhsin.listinghelper.ui.s3_photo_capture.list

import android.content.Context
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PhotoCaptureAdapter(
    val context: Context,
    val onPhotoClick: () -> Unit,
    val onPhotoRemoveClick: (uri: Uri) -> Unit,
): RecyclerView.Adapter<PhotoCaptureAdapter.ThumbnailViewHolder>() {

    private val list = mutableListOf<Uri>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
        val layout = PhotoCaptureItemLayout(context, onPhotoClick, onPhotoRemoveClick)
        return ThumbnailViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
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

    inner class ThumbnailViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val layout = itemView as PhotoCaptureItemLayout
    }
}