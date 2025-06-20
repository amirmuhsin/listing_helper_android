package com.amirmuhsin.listinghelper.ui.s4_bg_clean.list

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import coil.load
import coil.size.ViewSizeResolver
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.databinding.ItemLayoutPhotoPairBinding
import com.amirmuhsin.listinghelper.domain.model.PhotoPair
import com.amirmuhsin.listinghelper.util.getImageResolution
import com.amirmuhsin.listinghelper.util.getImageSizeInBytes
import com.amirmuhsin.listinghelper.util.getReadableSize

class BgCleanerItemLayout(
    context: Context,
    val photoClick: () -> Unit,
    val photoRemove: (uri: Uri) -> Unit,
): FrameLayout(context) {

    private val binding = ItemLayoutPhotoPairBinding.inflate(LayoutInflater.from(context), this, true)

    private var photoPair: PhotoPair? = null

    init {
        val height = resources.getDimensionPixelSize(R.dimen.photo_pair_height)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, height)
    }

    fun fillContent(photoPair: PhotoPair) {
        this.photoPair = photoPair

        binding.ivOriginal.load(photoPair.originalUri) {
            size(ViewSizeResolver(binding.ivCleaned))
            crossfade(true)
        }

        when (photoPair.bgCleanStatus) {
            PhotoPair.BgCleanStatus.PENDING -> {
                binding.pbLoading.visibility = GONE
                binding.ivCleaned.setImageResource(0)

                val fileSizeBytes = getImageSizeInBytes(context, photoPair.originalUri)
                val resolution = getImageResolution(context, photoPair.originalUri)

                Log.d("ImageInfo", "Original Size: ${getReadableSize(fileSizeBytes)}")
                Log.d("ImageInfo", "Original Resolution: ${resolution?.first} x ${resolution?.second}")
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

                val fileSizeBytes = getImageSizeInBytes(context, photoPair.cleanedUri!!)
                val resolution = getImageResolution(context, photoPair.cleanedUri!!)

                Log.d("ImageInfo", "Cleaned Size: ${getReadableSize(fileSizeBytes)}")
                Log.d("ImageInfo", "Cleaned Resolution: ${resolution?.first} x ${resolution?.second}")
            }

            PhotoPair.BgCleanStatus.FAILED -> {
                binding.pbLoading.visibility = GONE
                binding.ivCleaned.setImageResource(R.drawable.ic_report_16)
            }
        }
    }
}