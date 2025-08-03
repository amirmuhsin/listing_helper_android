package com.amirmuhsin.listinghelper.ui.common.fullscreen_image

import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.domain.model.PhotoPair
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

    fun deletePhotoPair(index: Int) {
        val currentList = _flPhotos.value.toMutableList()
        if (index in currentList.indices) {
            currentList.removeAt(index)

            // Reassign order starting from 1
            val reorderedList = currentList.mapIndexed { i, item ->
                item.copy(order = i + 1)
            }

            _flPhotos.value = reorderedList

            if (reorderedList.isEmpty()) {
                sendCommand(FullScreenCommands.AllImagesDeleted)
            } else {
                sendCommand(FullScreenCommands.ImageDeleted)
            }
        }
    }


}