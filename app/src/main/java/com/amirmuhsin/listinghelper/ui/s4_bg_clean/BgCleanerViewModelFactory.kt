package com.amirmuhsin.listinghelper.ui.s4_bg_clean

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amirmuhsin.listinghelper.networking.api.PhotoRoomService

class BgCleanerViewModelFactory(
    private val service: PhotoRoomService,
    private val appContext: Context
): ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BgCleanerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BgCleanerViewModel(service, appContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
