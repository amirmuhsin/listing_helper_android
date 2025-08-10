package com.amirmuhsin.listinghelper.ui.s2_0_product_detail.list

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import coil.load
import coil.size.ViewSizeResolver
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.databinding.ItemLayoutCleanedPhotoBinding
import com.amirmuhsin.listinghelper.domain.photo.PhotoPair
import com.amirmuhsin.listinghelper.domain.photo.PhotoPair.UploadStatus

class CleanedPhotoItemLayout(
    context: Context,
    val photoClick: (PhotoPair) -> Unit,
): FrameLayout(context) {

    private val binding = ItemLayoutCleanedPhotoBinding.inflate(LayoutInflater.from(context), this, true)

    private var currentPair: PhotoPair? = null

    init {
        setOnClickListener {
            currentPair?.let {
                photoClick(it)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    fun fillContent(pair: PhotoPair) {
        currentPair = pair

        binding.ivCleanedThumb.load(pair.cleanedUri) {
            size(ViewSizeResolver(binding.ivCleanedThumb))
            crossfade(true)
        }

        when (pair.uploadStatus) {
            UploadStatus.PENDING -> {
                binding.ivUploadStatus.isVisible = true
                binding.ivUploadStatus.setImageResource(R.drawable.ic_upload)
            }

            UploadStatus.UPLOADED -> {
                binding.ivUploadStatus.isVisible = true
                binding.ivUploadStatus.setImageResource(R.drawable.ic_check)
            }

            UploadStatus.FAILED -> {
                binding.ivUploadStatus.isVisible = true
                binding.ivUploadStatus.setImageResource(R.drawable.ic_warning)
            }

            else -> {

            }
        }
    }
}