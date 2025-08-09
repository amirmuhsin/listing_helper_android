package com.amirmuhsin.listinghelper.ui.s1_home.command

import com.amirmuhsin.listinghelper.core_views.events.command.Command

sealed class HomeCommands : Command{
    data class NewProductCreated(val productId: Long) : HomeCommands()
}