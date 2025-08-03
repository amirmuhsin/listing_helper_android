package com.amirmuhsin.listinghelper.ui.s5_review_upload.list

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amirmuhsin.listinghelper.domain.model.PhotoPair

class ReviewUploadAdapter(
    private val context: Context,
    private val onPhotoClick: (PhotoPair) -> Unit,
    private val onPhotoRemove: (PhotoPair) -> Unit,
    private val startDragListener: (RecyclerView.ViewHolder) -> Unit,
    val onReordered: (List<PhotoPair>) -> Unit
): ListAdapter<PhotoPair, ReviewUploadAdapter.VH>(DIFF), ItemTouchHelperAdapter {

    init {
        setHasStableIds(true)
    }

    private val internalList = mutableListOf<PhotoPair>()

    companion object {

        private val DIFF = object: DiffUtil.ItemCallback<PhotoPair>() {
            override fun areItemsTheSame(a: PhotoPair, b: PhotoPair) = a.internalId == b.internalId

            override fun areContentsTheSame(a: PhotoPair, b: PhotoPair) =
                a.order == b.order && a.cleanedUri == b.cleanedUri && a.uploadStatus == b.uploadStatus
        }
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).internalId.hashCode().toLong()
    }

    override fun submitList(list: List<PhotoPair>?) {
        super.submitList(list)
        internalList.clear()
        if (list != null) {
            internalList.addAll(list)
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition !in internalList.indices || toPosition !in internalList.indices) return false

        val item = internalList.removeAt(fromPosition)
        internalList.add(toPosition, item)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    fun getReorderedList(): List<PhotoPair> {
        return internalList.mapIndexed { index, item -> item.copy(order = index + 1) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val layout = ReviewUploadItemLayout(context, onPhotoClick, onPhotoRemove)
        val holder = VH(layout)
        return holder
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val pair = getItem(position)
        holder.layout.fillContent(pair) {
            startDragListener(holder)
        }
    }

    inner class VH(itemView: View): RecyclerView.ViewHolder(itemView) {

        val layout = itemView as ReviewUploadItemLayout
    }
}