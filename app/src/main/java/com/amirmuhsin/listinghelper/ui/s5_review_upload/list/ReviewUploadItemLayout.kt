package com.amirmuhsin.listinghelper.ui.s5_review_upload.list

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import coil.load
import coil.size.ViewSizeResolver
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.databinding.ItemLayoutConfirmationPhotoBinding
import com.amirmuhsin.listinghelper.domain.photo.PhotoPair
import com.amirmuhsin.listinghelper.domain.photo.PhotoPair.UploadStatus
import com.amirmuhsin.listinghelper.util.getImageResolution
import com.amirmuhsin.listinghelper.util.getImageSizeInBytes
import com.amirmuhsin.listinghelper.util.getReadableSize

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

    fun fillContent(pair: PhotoPair, startDragListener: () -> Unit) {
        current = pair

        val fileSizeBytes = getImageSizeInBytes(context, pair.cleanedUri)
        val resolution = getImageResolution(context, pair.cleanedUri)

        val imageResolution = resolution?.let { "${it.first} x ${it.second}" } ?: "Unknown"
        val imageSizeInKB = getReadableSize(fileSizeBytes)

        binding.tvOrder.text = pair.order.toString()
        binding.tvResolution.text = imageResolution
        binding.tvSize.text = imageSizeInKB

        binding.ivPhoto.load(pair.cleanedUri) {
            size(ViewSizeResolver(binding.ivPhoto))
            crossfade(true)
        }

        binding.pbUploading.isVisible = false
        binding.ivUploadStatus.isVisible = false
        when (pair.uploadStatus) {
            UploadStatus.PENDING -> {
                binding.ivUploadStatus.isVisible = true
                binding.ivUploadStatus.setImageResource(R.drawable.ic_upload)
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

        binding.btnHandle.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                startDragListener()
            }
            false
        }
    }
}