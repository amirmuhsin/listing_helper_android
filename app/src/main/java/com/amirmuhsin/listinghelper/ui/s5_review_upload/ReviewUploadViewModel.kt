package com.amirmuhsin.listinghelper.ui.s5_review_upload

import androidx.lifecycle.viewModelScope
import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.domain.photo.PhotoPair
import com.amirmuhsin.listinghelper.domain.photo.PhotoPairLocalRepository
import com.amirmuhsin.listinghelper.domain.product.ProductRemoteRepository
import com.amirmuhsin.listinghelper.ui.s5_review_upload.command.ReviewUploadCommands
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewUploadViewModel(
    private val photoPairLocalRepository: PhotoPairLocalRepository,
    private val productRemoteRepository: ProductRemoteRepository,
): BaseViewModel() {

    private val _pairs = MutableStateFlow<List<PhotoPair>>(emptyList())
    val pairs = _pairs.asStateFlow()

    private val _uploadProgress = MutableStateFlow(0)
    val uploadProgress = _uploadProgress.asStateFlow()

    private var productId: Long = -1L

    // our private mutable cache that the UI never sees directly
    private val inMemory: MutableList<PhotoPair> = mutableListOf()

    fun load(productId: Long) {
        this.productId = productId
        viewModelScope.launch {
            val fromDb = photoPairLocalRepository.getAllByProductId(productId)
                .sortedBy { it.order }

            // normalize to 1-based order
            val normalized = fromDb.mapIndexed { idx, p ->
                val correctOrder = idx + 1
                if (p.order != correctOrder) p.copy(order = correctOrder) else p
            }

            inMemory.clear()
            inMemory.addAll(normalized)
            _pairs.value = normalized

            // persist normalization quietly
            viewModelScope.launch {
                normalized.forEachIndexed { idx, p ->
                    photoPairLocalRepository.updateOrder(p.internalId, idx + 1)
                }
            }
        }
    }

    fun removePair(pair: PhotoPair) {
        inMemory.removeAll { it.internalId == pair.internalId }

        // reindex cache contiguously (1-based)
        for (i in inMemory.indices) {
            val order = i + 1
            if (inMemory[i].order != order) {
                inMemory[i] = inMemory[i].copy(order = order)
            }
        }

        _pairs.value = inMemory.toList()

        viewModelScope.launch {
            photoPairLocalRepository.delete(pair)
            inMemory.forEachIndexed { idx, p ->
                photoPairLocalRepository.updateOrder(p.internalId, idx + 1)
            }
        }
    }

    fun setReorderedPairsSilently(reordered: List<PhotoPair>) {
        inMemory.clear()
        inMemory.addAll(reordered.mapIndexed { idx, p -> p.copy(order = idx + 1) })

        viewModelScope.launch {
            inMemory.forEachIndexed { idx, p ->
                photoPairLocalRepository.updateOrder(p.internalId, idx + 1)
            }
        }
    }

    fun uploadAll(productItemId: Long) {
        val list = inMemory.toList() // snapshot of current order

        // set all pairs to UPLOADING
        println("hop: VM: updating all to UPLOADING")
        _pairs.value = _pairs.value.map { it.copy(uploadStatus = PhotoPair.UploadStatus.UPLOADING) }


        viewModelScope.launch {
            val total = list.size
            var uploaded = 0

            list.forEachIndexed { index, pair ->
                // start uploading
                try {
                    val imageAM = productRemoteRepository.uploadImage(productItemId, pair.internalId, pair.cleanedUri!!, "1-1-1")
                    // todo: assign the imageAM to the pair or return Image model from the upload function instead of ImageAM (define it in domain)

                    println("Image uploaded successfully: $imageAM")
                    val updatedPair = pair.copy(uploadStatus = PhotoPair.UploadStatus.UPLOADED, uploadItemId = imageAM.imageId)
                    println("hop: VM: updating pair to UPLOADED: $pair")
                    updatePair(updatedPair)

                    uploaded++
                    sendCommand(ReviewUploadCommands.UploadItemProgress(uploaded, total))
                } catch (e: Exception) {
                    showErrorSnackbar("Upload failed: ${e.message}")

                    val failedPair = pair.copy(uploadStatus = PhotoPair.UploadStatus.FAILED)
                    updatePair(failedPair)
                }

                // progress should be based on the upload count
                _uploadProgress.value = ((uploaded.toFloat() / total) * 100).toInt()
            }

            sendCommand(ReviewUploadCommands.UploadCompleted(uploaded, total))
        }
    }

    private fun updatePair(updated: PhotoPair) {
        // UI
        _pairs.value = _pairs.value.map {
            if (it.internalId == updated.internalId) {
                updated
            } else {
                it
            }
        }

        // Cache
        val idx = inMemory.indexOfFirst { it.internalId == updated.internalId }
        if (idx >= 0) inMemory[idx] = updated

        // DB
        viewModelScope.launch {
            photoPairLocalRepository.update(updated)
        }
    }
}