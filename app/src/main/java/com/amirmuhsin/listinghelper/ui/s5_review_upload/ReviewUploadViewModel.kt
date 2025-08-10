package com.amirmuhsin.listinghelper.ui.s5_review_upload

import androidx.lifecycle.viewModelScope
import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.domain.photo.PhotoPair
import com.amirmuhsin.listinghelper.domain.photo.PhotoPairLocalRepository
import com.amirmuhsin.listinghelper.domain.product.Product
import com.amirmuhsin.listinghelper.domain.product.ProductLocalRepository
import com.amirmuhsin.listinghelper.domain.product.ProductRemoteRepository
import com.amirmuhsin.listinghelper.ui.s5_review_upload.command.ReviewUploadCommands
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewUploadViewModel(
    private val productLocalRepository: ProductLocalRepository,
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
        // 1) remove from in-memory
        val removed = inMemory.removeAll { it.internalId == pair.internalId }
        if (!removed) return

        // 2) reindex memory (1-based) and track only changed items
        val changed = ArrayList<PhotoPair>()
        inMemory.forEachIndexed { i, item ->
            val want = i + 1
            if (item.order != want) {
                val updated = item.copy(order = want)
                inMemory[i] = updated
                changed += updated
            }
        }

        // 3) emit to UI immediately
        _pairs.value = inMemory.toList()

        // 4) persist using existing APIs
        viewModelScope.launch {
            try {
                photoPairLocalRepository.delete(pair)
                for (p in changed) {
                    photoPairLocalRepository.updateOrder(p.internalId, p.order)
                }
                productLocalRepository.updateImageCount(productId, inMemory.size)
            } catch (e: Exception) {
                // optional: revert UI or show a toast/snackbar
                // showErrorSnackbar("Failed to persist changes: ${e.message}")
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

    fun uploadAll(productId: Long) {
        // Only upload PENDING (and optionally FAILED on retry)
        val candidates =
            inMemory.filter { it.uploadStatus == PhotoPair.UploadStatus.PENDING || it.uploadStatus == PhotoPair.UploadStatus.FAILED }

        if (candidates.isEmpty()) {
            showErrorSnackbar("Nothing to upload.")
            sendCommand(ReviewUploadCommands.UploadCompleted(uploaded = 0, total = 0))
            return
        }

        // Mark only candidates as UPLOADING in the UI
        _pairs.value = _pairs.value.map { p ->
            if (candidates.any { it.internalId == p.internalId }) {
                p.copy(uploadStatus = PhotoPair.UploadStatus.UPLOADING)
            } else {
                p
            }
        }
        viewModelScope.launch {
            val total = candidates.size
            var uploaded = 0

            candidates.forEachIndexed { index, pair ->
                // start uploading
                try {
                    val imageAM = productRemoteRepository.uploadImage(
                        productId,
                        pair.internalId,
                        pair.cleanedUri!!,
                        "1-1-1"
                    )

                    val updatedPair = pair.copy(
                        uploadStatus = PhotoPair.UploadStatus.UPLOADED,
                        uploadItemId = imageAM.imageId
                    )
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

            val fullyDone = finalizeProductStatus(this@ReviewUploadViewModel.productId, uploaded, total)
            if (!fullyDone && uploaded != total) {
                showErrorSnackbar("Some images failed to upload. Please try again.")
            } else if (fullyDone) {
                sendCommand(ReviewUploadCommands.UploadFullyCompleted)
            }

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

    // In your ViewModel (or a small domain/use-case class)

    private suspend fun finalizeProductStatus(
        productId: Long,
        uploaded: Int,
        total: Int
    ): Boolean {
        val newStatus = if (uploaded == total) {
            Product.Status.DONE
        } else {
            Product.Status.HAS_FAILURE
        }
        productLocalRepository.updateStatus(productId, newStatus)
        return newStatus == Product.Status.DONE
    }
}