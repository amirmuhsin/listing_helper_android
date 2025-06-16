// ConfirmationItemLayout.kt
package com.amirmuhsin.listinghelper.ui.s5_review_upload.list

import android.content.Context
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import coil.load
import coil.size.ViewSizeResolver
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.databinding.ItemLayoutConfirmationPhotoBinding
import com.amirmuhsin.listinghelper.domain.model.PhotoPair
import com.amirmuhsin.listinghelper.domain.model.PhotoPair.UploadStatus

class ReviewUploadItemLayout(
    context: Context,
    val onPhotoClick: (PhotoPair) -> Unit,
    val onRemoveClick: (PhotoPair) -> Unit
): RelativeLayout(context) {

    private val binding = ItemLayoutConfirmationPhotoBinding.inflate(LayoutInflater.from(context), this, true)
    private var current: PhotoPair? = null

    init {
        binding.ivPhoto.setOnClickListener { current?.let(onPhotoClick) }
        binding.btnRemove.setOnClickListener { current?.let(onRemoveClick) }
    }

    fun fillContent(pair: PhotoPair) {
        println("hop: fillContent: ${pair.internalId} - ${pair.originalUri} - ${pair.cleanedUri} - ${pair.uploadStatus}")
        current = pair

        binding.tvOrder.text = pair.order.toString()
        binding.tvResolution.text = pair.resolution
        binding.tvSize.text = pair.sizeInBytes.toString()

        binding.ivPhoto.load(pair.cleanedUri) {
            size(ViewSizeResolver(binding.ivPhoto))
            crossfade(true)
        }

        binding.pbUploading.isVisible = false
        binding.ivUploadStatus.isVisible = false
        when (pair.uploadStatus) {
            UploadStatus.PENDING -> {
                // Do nothing, show default state
            }

            UploadStatus.UPLOADING -> {
                binding.pbUploading.isVisible = true
            }

            UploadStatus.UPLOADED -> {
                binding.ivUploadStatus.isVisible = true
                binding.ivUploadStatus.setImageResource(R.drawable.ic_check)
            }

            UploadStatus.FAILED -> {
                binding.ivUploadStatus.isVisible = true
                binding.ivUploadStatus.setImageResource(R.drawable.ic_warning)
            }
        }
    }
}