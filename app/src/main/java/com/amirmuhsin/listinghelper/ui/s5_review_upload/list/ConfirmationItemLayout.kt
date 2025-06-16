// ConfirmationItemLayout.kt
package com.amirmuhsin.listinghelper.ui.s5_review_upload.list

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import coil.load
import coil.size.ViewSizeResolver
import com.amirmuhsin.listinghelper.databinding.ItemLayoutConfirmationPhotoBinding
import com.amirmuhsin.listinghelper.domain.model.PhotoPair

class ConfirmationItemLayout(
    context: Context,
    val onClick: (PhotoPair) -> Unit,
    val onRemove: (PhotoPair) -> Unit
): FrameLayout(context) {

    private val binding = ItemLayoutConfirmationPhotoBinding
        .inflate(LayoutInflater.from(context), this)
    private var current: PhotoPair? = null

    init {
        binding.ivPhoto.setOnClickListener { current?.let(onClick) }
        binding.btnRemove.setOnClickListener { current?.let(onRemove) }
    }

    fun fillContent(pair: PhotoPair, order: Int) {
        current = pair
        binding.tvOrder.text = order.toString()
        binding.ivPhoto.load(pair.cleanedUri) {
            size(ViewSizeResolver(binding.ivPhoto))
            crossfade(true)
        }
        binding.pbItem.isVisible = !pair.isUploaded
        binding.ivCheck.isVisible = pair.isUploaded
    }
}