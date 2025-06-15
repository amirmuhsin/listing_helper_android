package com.amirmuhsin.listinghelper.ui.s3_photo_capture.list

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.widget.FrameLayout
import coil.load
import coil.size.ViewSizeResolver
import com.amirmuhsin.listinghelper.databinding.ItemLayoutPhotoBinding

class ThumbnailItemLayout(
    context: Context,
    val photoClick: () -> Unit,
    val photoRemove: (uri: Uri) -> Unit,
): FrameLayout(context) {

    private val binding = ItemLayoutPhotoBinding.inflate(LayoutInflater.from(context), this, true)

    private var currentUri: Uri? = null

    init {
        setOnClickListener {
            photoClick()
        }
        binding.btnRemove.setOnClickListener {
            currentUri?.let { photoRemove.invoke(it) }
        }
    }

    fun fillContent(uri: Uri) {
        currentUri = uri
        binding.ivThumb.setImageURI(uri)

        binding.ivThumb.load(uri) {
            size(ViewSizeResolver(binding.ivThumb))
            crossfade(true)
        }
    }
}