package com.amirmuhsin.listinghelper.ui.bg_removal

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amirmuhsin.listinghelper.networking.api.PhotoRoomService

class BgRemovalViewModelFactory(
    private val service: PhotoRoomService,
    private val appContext: Context
): ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BgRemovalViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BgRemovalViewModel(service, appContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
