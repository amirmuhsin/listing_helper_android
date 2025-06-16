package com.amirmuhsin.listinghelper.ui.s5_review_upload.command

import com.amirmuhsin.listinghelper.core_views.events.command.Command

sealed class ReviewUploadCommands: Command {
    data class UploadCompleted(val uploaded: Int, val total: Int): ReviewUploadCommands()
    data class UploadItemProgress(val uploaded: Int, val total: Int): ReviewUploadCommands()
}