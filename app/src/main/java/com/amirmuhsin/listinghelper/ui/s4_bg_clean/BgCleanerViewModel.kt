package com.amirmuhsin.listinghelper.ui.s4_bg_clean

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.domain.model.PhotoPair
import com.amirmuhsin.listinghelper.networking.api.PhotoRoomService
import com.amirmuhsin.listinghelper.util.copyUriToTempFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.util.UUID

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
                    originalUri = uri,
                    cleanedUri = null,
                    bgCleanStatus = PhotoPair.BgCleanStatus.PENDING,
                    order = index + 1,
                    resolution = "256x256 TEST",
                    sizeInBytes = 400,
                    imageType = ".png TEST"
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
                // 1) Read the file from cacheDir
                val originalUri = pair.originalUri
                val tempFile = copyUriToTempFile(appContext, originalUri) ?: return@launch

                // 2) Build MultipartBody.Part
                val requestFile = tempFile
                    .asRequestBody("image/jpeg".toMediaTypeOrNull())
                val multipart = MultipartBody.Part.createFormData(
                    "image_file", tempFile.name, requestFile
                )

                // 3) Optionally add other form parts (e.g. format=jpg)
                //    val formatPart = RequestBody.create("text/plain".toMediaTypeOrNull(), "jpg")

                try {
                    val response = service.cleanBackground(
                        image_file = multipart,
                        format = "png",   // or "jpg", "webp", or leave null for default
                        bgColor = "#FFFFFF", // if you want white background instead of transparency
                        size = "full",
                        crop = false
                    )
                    if (response.isSuccessful) {
                        // 4) Read raw bytes (PNG/JPG/WEBP)
                        val body = response.body() ?: return@launch
                        val bytes = body.bytes()

                        // write bytes to file so Coil can load from disk
                        val out = File(appContext.cacheDir, "cleaned_${pair.internalId}.png")
                        out.writeBytes(bytes)
                        val cleanedUri = out.toUri()

                        // 6) Store it and emit
                        _flPairs.value = _flPairs.value.map {
                            if (it.internalId == pair.internalId) {
                                it.copy(cleanedUri = cleanedUri, bgCleanStatus = PhotoPair.BgCleanStatus.COMPLETED)
                            } else {
                                it
                            }
                        }

                        calculateProgress()
                    } else {
                        // 7) Handle non-2xx (e.g. quota exceeded, bad request)
                        throw HttpException(response)
                    }
                } catch (e: Exception) {
                    //
                }
            }
        }
    }

    private fun calculateProgress() {
        val total = _flPairs.value.size
        if (total == 0) return
        val completed = _flPairs.value.count { it.bgCleanStatus == PhotoPair.BgCleanStatus.COMPLETED }
        _flProgress.value = (completed * 100) / total
    }

}