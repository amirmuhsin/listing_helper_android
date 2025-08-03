package com.amirmuhsin.listinghelper.ui.common.fullscreen_image.command

import com.amirmuhsin.listinghelper.core_views.events.command.Command

sealed class FullScreenCommands: Command {
    object AllImagesDeleted : FullScreenCommands()
    object ImageDeleted : FullScreenCommands()
}