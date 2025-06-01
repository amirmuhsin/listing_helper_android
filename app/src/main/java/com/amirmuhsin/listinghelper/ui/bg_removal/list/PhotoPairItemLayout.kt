package com.amirmuhsin.listinghelper.ui.bg_removal.list

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.widget.FrameLayout
import coil.load
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.databinding.ItemLayoutPhotoPairBinding

class PhotoPairItemLayout(
    context: Context,
    val photoClick: () -> Unit,
    val photoRemove: (uri: Uri) -> Unit,
): FrameLayout(context) {

    private val binding = ItemLayoutPhotoPairBinding.inflate(LayoutInflater.from(context), this, true)

    private var photoPair: PhotoPair? = null

    init {
        val height = resources.getDimensionPixelSize(R.dimen.photo_pair_height)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, height)
//        val marginLp = layoutParams as MarginLayoutParams
//        val smallPadding = resources.getDimensionPixelSize(R.dimen.very_small_padding)
//        marginLp.setMargins(smallPadding, smallPadding, smallPadding, smallPadding)
    }

    fun fillContent(photoPair: PhotoPair) {
        this.photoPair = photoPair
        binding.ivOriginal.load(photoPair.originalUri)
        if (photoPair.cleanedBitmap != null) {
            binding.ivCleaned.load(photoPair.cleanedBitmap)
        }
    }
}