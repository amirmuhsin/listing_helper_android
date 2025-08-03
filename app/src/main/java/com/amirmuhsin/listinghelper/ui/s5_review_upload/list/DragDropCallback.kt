package com.amirmuhsin.listinghelper.ui.s5_review_upload.list

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class DragDropCallback(
    private val myAdapter: ItemTouchHelperAdapter
):
    ItemTouchHelper.Callback() {

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (myAdapter is ReviewUploadAdapter) {
            val reordered = myAdapter.getReorderedList()
            myAdapter.submitList(reordered) {
                myAdapter.notifyDataSetChanged() // ← this disables animation artifacts
            }
            myAdapter.onReordered.invoke(reordered)
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return myAdapter.onItemMove(source.adapterPosition, target.adapterPosition)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Not used
    }

    override fun isLongPressDragEnabled(): Boolean = false // Disable long press
}
