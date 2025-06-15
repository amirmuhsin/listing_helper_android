package com.amirmuhsin.listinghelper.ui.bg_removal

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.networking.api.PhotoRoomService
import com.amirmuhsin.listinghelper.ui.bg_removal.list.PhotoPair
import com.amirmuhsin.listinghelper.util.copyUriToTempFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.util.UUID

class BgRemovalViewModel(
    private val service: PhotoRoomService,
    private val appContext: Context
): BaseViewModel() {

    private val _flPairs = MutableStateFlow<List<PhotoPair>>(emptyList())
    val flPairs = _flPairs.asStateFlow()

    private val _flProgress = MutableStateFlow<Int>(0)
    val flProgress = _flProgress.asStateFlow()

    fun initOriginals(uriList: List<Uri>) {
        val pairs = mutableListOf<PhotoPair>()
        uriList.forEach { uri ->
            val id = UUID.randomUUID().toString()
            pairs.add(PhotoPair(id, uri, null))
        }
        _flPairs.value = pairs
    }

    fun processAllOriginals() {
        // set all pairs to PROCESSING
        _flPairs.value = _flPairs.value.map { pair ->
            pair.copy(status = PhotoPair.Status.PROCESSING)
        }

        _flPairs.value.forEachIndexed { index, pair ->
            viewModelScope.launch {
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
                    val response = service.removeBackground(
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

                        // 5) Decode to Bitmap (on IO pool)
                        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                        // 6) Store it and emit
                        _flPairs.value = _flPairs.value.map {
                            if (it.id == pair.id) {
                                it.copy(cleanedBitmap = bmp, status = PhotoPair.Status.COMPLETED)
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
        val completed = _flPairs.value.count { it.status == PhotoPair.Status.COMPLETED }
        _flProgress.value = (completed * 100) / total
    }

}