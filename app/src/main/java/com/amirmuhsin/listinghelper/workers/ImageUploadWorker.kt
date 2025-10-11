package com.amirmuhsin.listinghelper.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.amirmuhsin.listinghelper.core_views.result.ResultError
import com.amirmuhsin.listinghelper.domain.photo.PhotoPair
import com.amirmuhsin.listinghelper.domain.photo.PhotoPairLocalRepository
import com.amirmuhsin.listinghelper.domain.product.ProductRemoteRepository
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.amirmuhsin.listinghelper.core_views.result.Result as AppResult

class ImageUploadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val productRemoteRepository: ProductRemoteRepository by inject()
    private val photoPairLocalRepository: PhotoPairLocalRepository by inject()

    override suspend fun doWork(): Result {
        val productId = inputData.getLong(KEY_PRODUCT_ID, -1L)
        if (productId == -1L) {
            return Result.failure(
                workDataOf(KEY_ERROR_MESSAGE to "Invalid product ID")
            )
        }

        // Get all photo pairs for this product that need uploading
        val photoPairs = photoPairLocalRepository.getPhotoPairsForProduct(productId).first()
        val pendingUploads = photoPairs.filter {
            it.uploadStatus == PhotoPair.UploadStatus.PENDING || it.uploadStatus == PhotoPair.UploadStatus.FAILED
        }

        if (pendingUploads.isEmpty()) {
            return Result.success(
                workDataOf(KEY_SUCCESS_MESSAGE to "No images to upload")
            )
        }

        var successCount = 0
        var failureCount = 0
        var lastError: String? = null

        for (photoPair in pendingUploads) {
            // Use cleaned URI if available, otherwise original
            val uri = photoPair.cleanedUri ?: photoPair.originalUri

            // Mark as uploading
            photoPairLocalRepository.updateUploadStatus(photoPair.internalId, PhotoPair.UploadStatus.UPLOADING)

            val uploadResult = productRemoteRepository.uploadImage(
                itemId = productId,
                photoId = photoPair.internalId,
                uri = uri,
                channelId = "1-1-1"
            )

            when (uploadResult) {
                is AppResult.Success -> {
                    // Update photo pair with server ID and mark as uploaded
                    photoPairLocalRepository.updateServerIdAndStatus(
                        localId = photoPair.internalId,
                        serverId = uploadResult.data.imageId.toString(),
                        status = PhotoPair.UploadStatus.UPLOADED
                    )
                    successCount++
                }
                is AppResult.Failure -> {
                    // Mark as failed and store error
                    photoPairLocalRepository.updateUploadStatus(
                        photoPair.internalId,
                        PhotoPair.UploadStatus.FAILED
                    )
                    failureCount++
                    lastError = when (val error = uploadResult.error) {
                        is ResultError.NetworkError -> {
                            if (error.isTimeout) "Upload timeout"
                            else "Network error: ${error.message}"
                        }
                        is ResultError.HttpError -> "Server error (${error.code}): ${error.message}"
                        else -> error.message
                    }
                }
            }
        }

        return when {
            failureCount == 0 -> Result.success(
                workDataOf(
                    KEY_SUCCESS_MESSAGE to "Successfully uploaded $successCount images",
                    KEY_SUCCESS_COUNT to successCount
                )
            )
            successCount > 0 -> Result.success(
                workDataOf(
                    KEY_SUCCESS_MESSAGE to "Uploaded $successCount images, $failureCount failed",
                    KEY_SUCCESS_COUNT to successCount,
                    KEY_FAILURE_COUNT to failureCount,
                    KEY_ERROR_MESSAGE to lastError
                )
            )
            else -> Result.retry() // Retry if all failed (transient network issue)
        }
    }

    companion object {
        const val KEY_PRODUCT_ID = "product_id"
        const val KEY_SUCCESS_MESSAGE = "success_message"
        const val KEY_ERROR_MESSAGE = "error_message"
        const val KEY_SUCCESS_COUNT = "success_count"
        const val KEY_FAILURE_COUNT = "failure_count"

        const val WORK_NAME_PREFIX = "image_upload_"
    }
}
