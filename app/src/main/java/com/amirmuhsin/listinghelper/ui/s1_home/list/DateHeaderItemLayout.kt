package com.amirmuhsin.listinghelper.ui.s1_home.list

import android.content.Context
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.amirmuhsin.listinghelper.databinding.ItemLayoutDateHeaderBinding

class DateHeaderItemLayout(context: Context): RelativeLayout(context) {

    private val binding = ItemLayoutDateHeaderBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    fun fillContent(date: String) {
        binding.tvDateHeader.text = date
    }
}
