package com.amirmuhsin.listinghelper.ui.common.fullscreen_image

import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.domain.photo.PhotoPair
import com.amirmuhsin.listinghelper.ui.common.fullscreen_image.command.FullScreenCommands
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FullScreenViewerViewModel(
    photoPairs: List<PhotoPair>,
    startIndex: Int
): BaseViewModel() {

    private val _flPhotos = MutableStateFlow(photoPairs)
    val flPhotos: StateFlow<List<PhotoPair>> = _flPhotos

    private val _flStartIndex = MutableStateFlow(startIndex)
    val flStartIndexFlow: StateFlow<Int> = _flStartIndex

    var isListChanged = false

    fun deletePhotoPair(index: Int) {
        val currentList = _flPhotos.value.toMutableList()
        if (index in currentList.indices) {
            currentList.removeAt(index)
            _flPhotos.value = currentList
            isListChanged = true

            if (currentList.isEmpty()) {
                sendCommand(FullScreenCommands.AllImagesDeleted)
            } else {
                sendCommand(FullScreenCommands.ImageDeleted)
            }
        }
    }
}