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

    private val _pairs = MutableStateFlow<List<PhotoPair>>(emptyList())
    val pairs = _pairs.asStateFlow()

    // pass index and the object as liveData
    private val _updatedPair = MutableStateFlow<Pair<Int, PhotoPair?>>(Pair(-1, null))
    val updatedPair = _updatedPair.asStateFlow()

    /**
     * Call this when you have your list of original URIs.
     * It initializes the cleanedBitmaps with the same size (all nulls).
     */
    fun initOriginals(uriList: List<Uri>) {
        val pairs = mutableListOf<PhotoPair>()
        uriList.forEach { uri ->
            val id = UUID.randomUUID().toString()
            pairs.add(PhotoPair(id, uri, null))
        }
        _pairs.value = pairs
    }

    /**
     * For each original URI, upload to PhotoRoom and decode the response into a Bitmap.
     * Emits each result into _cleanedBitmaps at the correct index.
     */
    fun processAllOriginals() {
        // set all pairs to PROCESSING
        _pairs.value = _pairs.value.map { pair ->
            pair.copy(status = PhotoPair.Status.PROCESSING)
        }

        viewModelScope.launch(Dispatchers.IO) {
            _pairs.value.forEachIndexed { index, pair ->
                // 1) Read the file from cacheDir
                val originalUri = pair.originalUri
                val tempFile = copyUriToTempFile(appContext, originalUri) ?: return@forEachIndexed

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
                        val body = response.body() ?: return@forEachIndexed
                        val bytes = body.bytes()

                        // 5) Decode to Bitmap (on IO pool)
                        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                        // 6) Store it and emit
                        pair.cleanedBitmap = bmp
                        pair.status = PhotoPair.Status.COMPLETED
                        // emit that pair individually
                        _updatedPair.value = Pair(index, pair)
                    } else {
                        // 7) Handle non-2xx (e.g. quota exceeded, bad request)
                        throw HttpException(response)
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

}