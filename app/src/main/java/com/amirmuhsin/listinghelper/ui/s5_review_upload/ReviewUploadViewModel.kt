package com.amirmuhsin.listinghelper.ui.s5_review_upload

import androidx.lifecycle.viewModelScope
import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.domain.model.PhotoPair
import com.amirmuhsin.listinghelper.domain.product.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewUploadViewModel(
    private val repo: ProductRepository,
): BaseViewModel() {

    private val _pairs = MutableStateFlow<List<PhotoPair>>(emptyList())
    val pairs = _pairs.asStateFlow()

    private val _uploadProgress = MutableStateFlow(0)
    val uploadProgress = _uploadProgress.asStateFlow()

    fun setInitialPairs(list: List<PhotoPair>) {
        _pairs.value = list
    }

    fun removePair(pair: PhotoPair) {
        _pairs.value = _pairs.value.filter { it.internalId != pair.internalId }
    }

    fun uploadAll(productItemId: Long) {
        val list = _pairs.value

        viewModelScope.launch {
            showProgressDialog()
            val total = list.size
            list.forEachIndexed { index, pair ->
                // update the status to UPLOADING
                pair.uploadStatus = PhotoPair.UploadStatus.UPLOADING
                updatePair(pair)

                // start uploading
                try {
                    val imageAM = repo.uploadImage(productItemId, pair.cleanedUri!!, "1-1-1")
                    // todo: assign the imageAM to the pair or return Image model from the upload function instead of ImageAM (define it in domain)

                    println("Image uploaded successfully: $imageAM")
                    pair.uploadStatus = PhotoPair.UploadStatus.UPLOADED
                    updatePair(pair)

                } catch (e: Exception) {
                    showErrorSnackbar("Upload failed: ${e.message}")

                    pair.uploadStatus = PhotoPair.UploadStatus.FAILED
                    updatePair(pair)
                }
                _uploadProgress.value = ((index + 1) * 100) / total
            }
            hideProgressDialog()
        }
    }

    private fun updatePair(updated: PhotoPair) {
        _pairs.value = _pairs.value.map { if (it.internalId == updated.internalId) updated else it }
    }

}