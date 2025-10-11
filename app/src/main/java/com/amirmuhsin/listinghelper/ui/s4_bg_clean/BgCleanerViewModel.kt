package com.amirmuhsin.listinghelper.ui.s4_bg_clean

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.core_views.result.Result
import com.amirmuhsin.listinghelper.core_views.result.ResultError
import com.amirmuhsin.listinghelper.domain.photo.PhotoPair
import com.amirmuhsin.listinghelper.data.networking.api.PhotoRoomService
import com.amirmuhsin.listinghelper.util.copyUriToTempFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import java.util.UUID

/**
 * DEPRECATED: This ViewModel uses PhotoRoom API which is no longer in use.
 * Kept for backward compatibility.
 */
@Suppress("DEPRECATION")
class BgCleanerViewModel(
    private val service: PhotoRoomService,
    private val appContext: Context
): BaseViewModel() {

    private val _flPairs = MutableStateFlow<List<PhotoPair>>(emptyList())
    val flPairs = _flPairs.asStateFlow()

    private val _flProgress = MutableStateFlow<Int>(0)
    val flProgress = _flProgress.asStateFlow()

    fun initOriginals(uriList: List<Uri>) {
        val pairs = mutableListOf<PhotoPair>()
        uriList.forEachIndexed { index, uri ->
            val id = UUID.randomUUID().toString()
            pairs.add(
                PhotoPair(
                    internalId = id,
                    productId = -1,
                    originalUri = uri,
                    cleanedUri = null,
                    bgCleanStatus = PhotoPair.BgCleanStatus.PENDING,
                    order = index + 1
                )
            )
        }
        _flPairs.value = pairs
    }

    fun processAllOriginals() {
        // set all pairs to PROCESSING
        _flPairs.value = _flPairs.value.map { pair ->
            pair.copy(bgCleanStatus = PhotoPair.BgCleanStatus.PROCESSING)
        }

        _flPairs.value.forEachIndexed { index, pair ->
            viewModelScope.launch(Dispatchers.IO) {
                val result = processImage(pair)

                result
                    .onSuccess { cleanedUri ->
                        // Update UI with successful result
                        _flPairs.value = _flPairs.value.map {
                            if (it.internalId == pair.internalId) {
                                it.copy(cleanedUri = cleanedUri, bgCleanStatus = PhotoPair.BgCleanStatus.COMPLETED)
                            } else {
                                it
                            }
                        }
                        calculateProgress()
                    }
                    .onFailure { error ->
                        // Mark this pair as failed and show error
                        _flPairs.value = _flPairs.value.map {
                            if (it.internalId == pair.internalId) {
                                it.copy(bgCleanStatus = PhotoPair.BgCleanStatus.FAILED)
                            } else {
                                it
                            }
                        }

                        // Show appropriate error message based on error type
                        when (error) {
                            is ResultError.NetworkError -> {
                                if (error.isTimeout) {
                                    showErrorSnackbar("Background removal timed out for image ${index + 1}")
                                } else {
                                    showErrorSnackbar("Network error for image ${index + 1}: ${error.message}")
                                }
                            }
                            is ResultError.HttpError -> {
                                showErrorSnackbar("Server error for image ${index + 1}: ${error.message}")
                            }
                            else -> {
                                showErrorSnackbar("Failed to process image ${index + 1}: ${error.message}")
                            }
                        }
                        calculateProgress()
                    }
            }
        }
    }

    private suspend fun processImage(pair: PhotoPair): Result<Uri> = Result.runCatching {
        // 1) Read the file from cacheDir
        val originalUri = pair.originalUri
        val tempFile = copyUriToTempFile(appContext, originalUri)
            ?: throw IllegalArgumentException("Cannot copy URI to temp file: $originalUri")

        // 2) Build MultipartBody.Part
        val requestFile = tempFile
            .asRequestBody("image/jpeg".toMediaTypeOrNull())
        val multipart = MultipartBody.Part.createFormData(
            "imageFile", tempFile.name, requestFile
        )

        // 3) Make API call
        val response = service.editImage(imageFile = multipart)

        // 4) Process response
        processImageResponse(response, pair)
    }

    private fun processImageResponse(response: Response<ResponseBody>, pair: PhotoPair): Uri {
        if (!response.isSuccessful) {
            throw retrofit2.HttpException(response)
        }

        // Read raw bytes (PNG/JPG/WEBP)
        val body = response.body() ?: throw IllegalStateException("Response body is null")
        val bytes = body.bytes()

        // Write bytes to file so Coil can load from disk
        val out = File(appContext.cacheDir, "cleaned_${pair.internalId}.jpg")
        out.writeBytes(bytes)

        return out.toUri()
    }

    private fun calculateProgress() {
        val total = _flPairs.value.size
        if (total == 0) return
        val completed = _flPairs.value.count { it.bgCleanStatus == PhotoPair.BgCleanStatus.COMPLETED }
        _flProgress.value = (completed * 100) / total
    }

}