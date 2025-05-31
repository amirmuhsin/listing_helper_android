package com.amirmuhsin.listinghelper.core_views.divider

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity.CENTER
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.amirmuhsin.listinghelper.R
import com.amirmuhsin.listinghelper.databinding.ViewDividerBinding

class DividerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
): LinearLayout(context, attrs) {

    private val binding: ViewDividerBinding = ViewDividerBinding.inflate(LayoutInflater.from(context), this)

    init {
        readAttributes(attrs)
        orientation = HORIZONTAL
        gravity = CENTER

        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        setLayoutParams(layoutParams)
    }

    private fun readAttributes(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DividerView)
        val title = typedArray.getText(R.styleable.DividerView_android_text)
        binding.tvTitle.text = title
        typedArray.recycle()
    }

}