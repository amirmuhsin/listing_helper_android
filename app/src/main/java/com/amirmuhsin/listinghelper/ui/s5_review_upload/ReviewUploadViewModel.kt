package com.amirmuhsin.listinghelper.ui.s5_review_upload

import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.domain.photo.PhotoPair
import com.amirmuhsin.listinghelper.domain.photo.PhotoPairLocalRepository
import com.amirmuhsin.listinghelper.domain.product.Product
import com.amirmuhsin.listinghelper.domain.product.ProductLocalRepository
import com.amirmuhsin.listinghelper.ui.s5_review_upload.command.ReviewUploadCommands
import com.amirmuhsin.listinghelper.workers.ImageUploadWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReviewUploadViewModel(
    private val productLocalRepository: ProductLocalRepository,
    private val photoPairLocalRepository: PhotoPairLocalRepository,
    private val workManager: WorkManager,
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
            // Update database status to UPLOADING for candidates
            candidates.forEach { pair ->
                photoPairLocalRepository.updateUploadStatus(
                    pair.internalId,
                    PhotoPair.UploadStatus.UPLOADING
                )
            }

            // Create WorkManager request for background upload
            val inputData = Data.Builder()
                .putLong(ImageUploadWorker.KEY_PRODUCT_ID, productId)
                .build()

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val uploadWorkRequest = OneTimeWorkRequestBuilder<ImageUploadWorker>()
                .setInputData(inputData)
                .setConstraints(constraints)
                .build()

            // Enqueue work with unique name to prevent duplicate uploads
            val workName = "${ImageUploadWorker.WORK_NAME_PREFIX}$productId"
            workManager.enqueueUniqueWork(
                workName,
                ExistingWorkPolicy.KEEP, // Keep existing work if already running
                uploadWorkRequest
            )

            // Observe work status
            workManager.getWorkInfoByIdLiveData(uploadWorkRequest.id)
                .observeForever { workInfo ->
                    when (workInfo?.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            val successCount = workInfo.outputData.getInt(ImageUploadWorker.KEY_SUCCESS_COUNT, 0)
                            val failureCount = workInfo.outputData.getInt(ImageUploadWorker.KEY_FAILURE_COUNT, 0)
                            val total = successCount + failureCount

                            _uploadProgress.value = 100
                            sendCommand(ReviewUploadCommands.UploadCompleted(successCount, total))

                            viewModelScope.launch {
                                // Reload pairs to reflect updated statuses from WorkManager
                                load(productId)

                                val fullyDone = finalizeProductStatus(productId, successCount, total)
                                if (!fullyDone && successCount != total) {
                                    showErrorSnackbar("Some images failed to upload. Please try again.")
                                } else if (fullyDone) {
                                    sendCommand(ReviewUploadCommands.UploadFullyCompleted)
                                }
                            }
                        }
                        WorkInfo.State.FAILED -> {
                            val errorMessage = workInfo.outputData.getString(ImageUploadWorker.KEY_ERROR_MESSAGE)
                            showErrorSnackbar("Upload failed: ${errorMessage ?: "Unknown error"}")
                            sendCommand(ReviewUploadCommands.UploadCompleted(uploaded = 0, total = candidates.size))
                        }
                        WorkInfo.State.RUNNING -> {
                            // Could update progress here if worker provides intermediate updates
                        }
                        else -> {
                            // ENQUEUED, BLOCKED, CANCELLED states
                        }
                    }
                }

            showSuccessSnackbar("Upload started in background. You can close the app safely.")
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