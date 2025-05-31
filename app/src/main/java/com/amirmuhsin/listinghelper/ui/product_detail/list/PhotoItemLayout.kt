package com.amirmuhsin.listinghelper.ui.product_detail.list

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.widget.FrameLayout
import coil.load
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.databinding.ItemLayoutPhotoBinding

class PhotoItemLayout(
    context: Context,
    val photoClick: () -> Unit,
    val photoRemove: (uri: Uri) -> Unit,
): FrameLayout(context) {

    private val binding = ItemLayoutPhotoBinding.inflate(LayoutInflater.from(context), this)

    private var currentUri: Uri? = null

    init {
        layoutParams = LayoutParams(getCellSizeInPx(context), getCellSizeInPx(context))
        val marginLp = layoutParams as MarginLayoutParams
        val smallPadding = resources.getDimensionPixelSize(R.dimen.very_small_padding)
        marginLp.setMargins(smallPadding, smallPadding, smallPadding, smallPadding)

        isClickable = true
        isFocusable = true

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

        val sizePx = getCellSizeInPx(context)

        binding.ivThumb.load(uri) {
            size(sizePx, sizePx)
            crossfade(true)
        }
    }

    companion object {

        const val cellSizeInDP = 64
        var cellSizeInPx = 0

        fun getCellSizeInPx(context: Context): Int {
            return if (cellSizeInPx == 0) {
                val scale = context.resources.displayMetrics.density
                cellSizeInPx = (cellSizeInDP * scale + 0.5f).toInt()
                cellSizeInPx
            } else {
                cellSizeInPx
            }
        }
    }
}