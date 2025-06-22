package com.amirmuhsin.listinghelper.ui.s2_product_detail.list

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.amirmuhsin.listinghelper.databinding.ItemLayoutAddCleanedPhotoBinding

class AddPhotoItemLayout(
    context: Context,
    val addClick: () -> Unit,
): FrameLayout(context) {

    private val binding = ItemLayoutAddCleanedPhotoBinding.inflate(LayoutInflater.from(context), this, true)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    init {
        setOnClickListener {
            addClick.invoke()
        }
    }
}