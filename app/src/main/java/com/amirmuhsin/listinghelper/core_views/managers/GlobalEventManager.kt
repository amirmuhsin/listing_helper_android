package com.amirmuhsin.listinghelper.core_views.managers

import android.os.Bundle
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object GlobalEventManager {

    private val _fEvents = MutableSharedFlow<Pair<String, Bundle?>>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val fEvents = _fEvents.asSharedFlow()

    fun sendNewEvent(event: Pair<String, Bundle?>) {
        _fEvents.tryEmit(event)
    }

    object GlobalEvent {

        const val LogOut = "log_out"
        const val LogOutError = "log_out_error"
        const val VersionUpdateRequired = "open_forced_update_dialog"
    }
}