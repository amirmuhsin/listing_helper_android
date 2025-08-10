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
) : BaseViewModel() {

    private val _flPhotos = MutableStateFlow<Pair<List<PhotoPair>, Int>?>(null)
    val flPhotos: StateFlow<Pair<List<PhotoPair>, Int>?> = _flPhotos

    // keep a local cache to operate on
    private var photoPairs: List<PhotoPair> = emptyList()

    var isListChanged = false
        private set

    init {
        getPhotoPairs()
    }

    private fun getPhotoPairs() {
        viewModelScope.launch {
            photoPairs = photoPairLocalRepository
                .getAllByProductId(productId)
                .sortedBy { it.order }

            val initialIndex = photoPairs.indexOfFirst { it.internalId == startPhotoPairId }
            // emit photos + start index (or -1 if not found so Fragment can skip jump)
            _flPhotos.value = photoPairs to if (initialIndex >= 0) initialIndex else -1
        }
    }

    fun deletePhotoPair(index: Int) {
        viewModelScope.launch {
            if (index !in photoPairs.indices) return@launch

            val victim = photoPairs[index]

            // 1) delete DB row
            photoPairLocalRepository.delete(victim)

            // 2) best-effort delete local files we own
            victim.cleanedUri?.let { ImageStore.deleteIfManaged(appContext, it) }
            ImageStore.deleteIfManaged(appContext, victim.originalUri)

            // 3) update local cache + emit (with startIndex = -1 so UI keeps current position)
            val updated = photoPairs.toMutableList().apply { removeAt(index) }.toList()
            photoPairs = updated
            _flPhotos.value = updated to -1

            isListChanged = true
            if (updated.isEmpty()) {
                sendCommand(FullScreenCommands.AllImagesDeleted)
            } else {
                sendCommand(FullScreenCommands.ImageDeleted)
            }
        }
    }
}
