package com.amirmuhsin.listinghelper.ui.s1_home.list

import android.content.Context
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.amirmuhsin.listinghelper.databinding.ItemLayoutEmptyStateBinding

class EmptyStateItemLayout(context: Context) : RelativeLayout(context) {

    private val binding = ItemLayoutEmptyStateBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    fun fillContent(message: String) {
        binding.tvEmptyMessage.text = message
    }
}
