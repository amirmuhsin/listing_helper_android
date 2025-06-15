package com.amirmuhsin.listinghelper.ui.s2_product_detail.list

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class PhotosAdapter(val context: Context) : RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val layout = PhotoItemLayout(context, {}, {})
        return PhotoViewHolder(layout)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    inner class PhotoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val layout = itemView as PhotoItemLayout
    }

}