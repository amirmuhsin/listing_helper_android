package com.amirmuhsin.listinghelper.ui.s5_review_upload

import androidx.lifecycle.viewModelScope
import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.domain.model.PhotoPair
import com.amirmuhsin.listinghelper.domain.product.ProductRepository
import com.amirmuhsin.listinghelper.ui.s5_review_upload.command.ReviewUploadCommands
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

    fun setPhotoPairs(list: List<PhotoPair>) {
        _pairs.value = list
    }

    fun removePair(pair: PhotoPair) {
        _pairs.value = _pairs.value.filter { it.internalId != pair.internalId }
    }

    fun setReorderedPairsSilently(reordered: List<PhotoPair>) {
        (_pairs.value as? MutableList<PhotoPair>)?.apply {
            clear()
            addAll(reordered)
        }
    }

    fun uploadAll(productItemId: Long) {
        val list = _pairs.value

        // set all pairs to UPLOADING
        println("hop: VM: updating all to UPLOADING")
        _pairs.value = _pairs.value.map { pair ->
            pair.copy(uploadStatus = PhotoPair.UploadStatus.UPLOADING)
        }

        var uploadCount = 0

        viewModelScope.launch {
            val total = list.size
            list.forEachIndexed { index, pair ->
                // start uploading
                try {
                    val imageAM = repo.uploadImage(productItemId, pair.cleanedUri!!, "1-1-1")
                    // todo: assign the imageAM to the pair or return Image model from the upload function instead of ImageAM (define it in domain)

                    println("Image uploaded successfully: $imageAM")
                    val updatedPair = pair.copy(uploadStatus = PhotoPair.UploadStatus.UPLOADED)
                    println("hop: VM: updating pair to UPLOADED: $pair")
                    updatePair(updatedPair)

                    uploadCount++
                    sendCommand(ReviewUploadCommands.UploadItemProgress(uploadCount, total))
                } catch (e: Exception) {
                    showErrorSnackbar("Upload failed: ${e.message}")

                    val failedPair = pair.copy(uploadStatus = PhotoPair.UploadStatus.FAILED)
                    updatePair(failedPair)
                }

                // progress should be based on the upload count
                _uploadProgress.value = ((uploadCount.toFloat() / total) * 100).toInt()
            }

            sendCommand(ReviewUploadCommands.UploadCompleted(uploadCount, total))
        }
    }

    private fun updatePair(updated: PhotoPair) {
        _pairs.value = _pairs.value.map {
            if (it.internalId == updated.internalId) {
                updated
            } else {
                it
            }
        }
    }
}