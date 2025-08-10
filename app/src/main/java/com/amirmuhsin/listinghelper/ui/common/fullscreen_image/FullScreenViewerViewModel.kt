package com.amirmuhsin.listinghelper.ui.common.fullscreen_image

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.amirmuhsin.listinghelper.core_views.base.viewmodel.BaseViewModel
import com.amirmuhsin.listinghelper.domain.photo.PhotoPair
import com.amirmuhsin.listinghelper.domain.photo.PhotoPairLocalRepository
import com.amirmuhsin.listinghelper.ui.common.fullscreen_image.command.FullScreenCommands
import com.amirmuhsin.listinghelper.util.ImageStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FullScreenViewerViewModel(
    private val appContext: Context,
    val productId: Long,
    val startPhotoPairId: String,
    val photoPairLocalRepository: PhotoPairLocalRepository
): BaseViewModel() {

    private val _flPhotos = MutableStateFlow(emptyList<PhotoPair>())
    val flPhotos: StateFlow<List<PhotoPair>> = _flPhotos

    private val _flStartIndex = MutableStateFlow(-1)
    val flStartIndexFlow: StateFlow<Int> = _flStartIndex

    var isListChanged = false
        private set

    init {
        getPhotoPairs()
    }

    private fun getPhotoPairs() {
        viewModelScope.launch {
            val allPhotoPairs = photoPairLocalRepository.getAllByProductId(productId).sortedBy { it.order }
            _flPhotos.value = allPhotoPairs

            val startIndex = allPhotoPairs.indexOfFirst { it.internalId == startPhotoPairId }
            if (startIndex != -1) {
                _flStartIndex.value = startIndex
            } else {
                _flStartIndex.value = 0
            }
        }
    }

    fun deletePhotoPair(index: Int) {
        viewModelScope.launch {
            val current = _flPhotos.value.toMutableList()
            if (index !in current.indices) return@launch

            val victim = current[index]
            // 1) delete DB row
            photoPairLocalRepository.delete(victim)

            // 2) best-effort delete local files if they are our managed copies
            victim.cleanedUri?.let { ImageStore.deleteIfManaged(appContext, it) }
            ImageStore.deleteIfManaged(appContext, victim.originalUri)

            // 3) update UI
            current.removeAt(index)
            _flPhotos.value = current
            isListChanged = true
            if (current.isEmpty()) {
                sendCommand(FullScreenCommands.AllImagesDeleted)
            } else {
                sendCommand(FullScreenCommands.ImageDeleted)
            }
        }
    }
}