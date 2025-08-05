package com.amirmuhsin.listinghelper.ui.common.fullscreen_image

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amirmuhsin.listinghelper.domain.model.PhotoPair

class FullScreenViewerViewModelFactory(
    private val photoPairs: List<PhotoPair>,
    private val startIndex: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FullScreenViewerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FullScreenViewerViewModel(
                photoPairs = photoPairs,
                startIndex = startIndex
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.name}")
    }
}
