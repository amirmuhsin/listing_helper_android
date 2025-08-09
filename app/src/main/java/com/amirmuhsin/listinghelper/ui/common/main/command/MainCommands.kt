package com.amirmuhsin.listinghelper.ui.common.main.command

import com.amirmuhsin.listinghelper.core_views.events.command.Command

sealed class MainCommands: Command {
    data class NewProductWithImagesCreated(
        val productId: Long,
    ): MainCommands()
}