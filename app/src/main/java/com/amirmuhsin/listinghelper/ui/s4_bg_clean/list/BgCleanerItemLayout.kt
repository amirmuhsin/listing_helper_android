package com.amirmuhsin.listinghelper.ui.s4_bg_clean.list

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.widget.FrameLayout
import coil.load
import coil.size.ViewSizeResolver
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.databinding.ItemLayoutPhotoPairBinding
import com.amirmuhsin.listinghelper.domain.photo.PhotoPair
import com.amirmuhsin.listinghelper.util.getImageResolution
import com.amirmuhsin.listinghelper.util.getImageSizeInBytes
import com.amirmuhsin.listinghelper.util.getReadableSize

class BgCleanerItemLayout(
    context: Context,
    val photoClick: () -> Unit,
    val photoRemove: (uri: Uri) -> Unit,
): FrameLayout(context) {

    private val binding = ItemLayoutPhotoPairBinding.inflate(LayoutInflater.from(context), this, true)

    private var currentPhotoPair: PhotoPair? = null

    init {
        calcImageSize(context)

        val layoutParamsOriginal = binding.ivOriginal.layoutParams
        layoutParamsOriginal.width = cellSizeInPx
        layoutParamsOriginal.height = cellSizeInPx
        binding.ivOriginal.layoutParams = layoutParamsOriginal

        val layoutParamsCleaned = binding.ivCleaned.layoutParams
        layoutParamsCleaned.width = cellSizeInPx
        layoutParamsCleaned.height = cellSizeInPx
        binding.ivCleaned.layoutParams = layoutParamsCleaned
    }

    fun fillContent(photoPair: PhotoPair) {
        this.currentPhotoPair = photoPair

        binding.ivOriginal.load(photoPair.originalUri) {
            size(ViewSizeResolver(binding.ivCleaned))
            crossfade(true)
        }

        setOriginalImageInfo(photoPair)

        when (photoPair.bgCleanStatus) {
            PhotoPair.BgCleanStatus.PENDING -> {
                binding.pbLoading.visibility = GONE
                binding.ivCleaned.setImageResource(0)
            }

            PhotoPair.BgCleanStatus.PROCESSING -> {
                binding.pbLoading.visibility = VISIBLE
                binding.ivCleaned.setImageResource(0)
            }

            PhotoPair.BgCleanStatus.COMPLETED -> {
                binding.pbLoading.visibility = GONE
                binding.ivCleaned.load(photoPair.cleanedUri) {
                    size(binding.ivCleaned.width, binding.ivCleaned.height)
                    crossfade(true)
                }

                setCleanedImageInfo(photoPair)
            }

            PhotoPair.BgCleanStatus.FAILED -> {
                binding.pbLoading.visibility = GONE
                binding.ivCleaned.setImageResource(R.drawable.ic_report_16)
            }
        }
    }

    private fun setOriginalImageInfo(photoPair: PhotoPair) {
        val fileSizeBytes = getImageSizeInBytes(context, photoPair.originalUri)
        val resolution = getImageResolution(context, photoPair.originalUri)

        val imageResolution = resolution?.let { "${it.first} x ${it.second}" } ?: "Unknown"
        val imageSizeInKB = getReadableSize(fileSizeBytes)
        binding.tvOriginalImageInfo.text = imageResolution + " | " + imageSizeInKB
    }

    private fun setCleanedImageInfo(photoPair: PhotoPair) {
        val fileSizeBytes = getImageSizeInBytes(context, photoPair.cleanedUri!!)
        val resolution = getImageResolution(context, photoPair.cleanedUri!!)

        val imageResolution = resolution?.let { "${it.first} x ${it.second}" } ?: "Unknown"
        val imageSizeInKB = getReadableSize(fileSizeBytes)
        binding.tvCleanedImageInfo.text = imageResolution + " | " + imageSizeInKB
    }

    companion object {

        private var cellSizeInPx = 0

        fun calcImageSize(context: Context) {
            if (cellSizeInPx > 0) return // Already calculated
            // 1. Get screen width
            val screenWidth = context.resources.displayMetrics.widthPixels

            // 2. Calculate margins
            // || 8dp | image | 4dp || 4dp | image | 8dp ||
            val outerMargin = context.resources.getDimensionPixelSize(R.dimen.small_padding)
            val innerMargin = context.resources.getDimensionPixelSize(R.dimen.very_small_padding)
            val totalMargin = 2 * (outerMargin + innerMargin)

            // 3. Calculate final available width for images
            val availableWidth = screenWidth - totalMargin
            val imageSize = availableWidth / 2

            cellSizeInPx = imageSize
        }
    }
}