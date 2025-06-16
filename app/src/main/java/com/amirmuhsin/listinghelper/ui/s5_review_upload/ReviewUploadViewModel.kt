package com.amirmuhsin.listinghelper.ui.s5_review_upload

import androidx.lifecycle.viewModelScope
import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.domain.model.PhotoPair
import com.amirmuhsin.listinghelper.domain.product.ProductRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewUploadViewModel(
    private val repo: ProductRepository
): BaseViewModel() {

    private val _pairs = MutableStateFlow<List<PhotoPair>>(emptyList())
    val pairs = _pairs.asStateFlow()

    private val _uploadProgress = MutableStateFlow(0)
    val uploadProgress = _uploadProgress.asStateFlow()

    private val _uploadedIds = MutableSharedFlow<String>()
    val uploadedIds = _uploadedIds.asSharedFlow()

    fun setInitialPairs(list: List<PhotoPair>) {
        _pairs.value = list
    }

    fun removePair(pair: PhotoPair) {
        _pairs.value = _pairs.value.filter { it.internalId != pair.internalId }
    }

    fun uploadAll(productItemId: Long, list: List<PhotoPair>) {
        viewModelScope.launch {
            showProgressDialog()
            val total = list.size
            list.forEachIndexed { idx, pair ->
                try {
                    val image = repo.uploadImage(productItemId, pair.cleanedUri!!, "1-1-1")
                    _uploadedIds.emit(pair.internalId)
                } catch (e: Exception) {
                    showErrorSnackbar("Upload failed: ${e.message}")
                }
                _uploadProgress.value = ((idx + 1) * 100) / total
            }
            hideProgressDialog()
        }
    }
}

///**
// * Returns a Callback to enable drag & drop reordering in the RecyclerView.
// */
//fun ReviewUploadViewModel.dragCallback(adapter: ConfirmationAdapter): ItemTouchHelper.Callback {
//    return object: ItemTouchHelper.SimpleCallback(
//        ItemTouchHelper.UP or ItemTouchHelper.DOWN,
//        0
//    ) {
//        override fun onMove(
//            recyclerView: RecyclerView,
//            viewHolder: RecyclerView.ViewHolder,
//            target: RecyclerView.ViewHolder
//        ): Boolean {
//            val from = viewHolder.bindingAdapterPosition
//            val to = target.bindingAdapterPosition
//            // Swap items in the ViewModel state
//            val updated = _pairs.value.toMutableList().apply { Collections.swap(this, from, to) }
//            _pairs.value = updated
//            adapter.notifyItemMoved(from, to)
//            return true
//        }
//
//        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//            // No swipe actions
//        }
//    }
//}