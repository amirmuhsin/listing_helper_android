package com.amirmuhsin.listinghelper.ui.photo_capture

import android.net.Uri
import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PhotoCaptureViewModel: BaseViewModel() {

    private val _photos = MutableStateFlow<List<Uri>>(emptyList())
    val photosFlow = _photos.asStateFlow()

    fun addPhoto(uri: Uri) {
        _photos.value = _photos.value + uri
    }

    fun removePhoto(uri: Uri) {
        _photos.value = _photos.value.filterNot { it == uri }
    }

}