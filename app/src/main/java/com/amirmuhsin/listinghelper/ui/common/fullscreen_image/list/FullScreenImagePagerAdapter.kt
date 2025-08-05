package com.amirmuhsin.listinghelper.ui.common.fullscreen_image.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.amirmuhsin.listinghelper.databinding.ItemFullScreenImageBinding
import com.amirmuhsin.listinghelper.domain.photo.PhotoPair

class FullScreenImagePagerAdapter(
    private val onUiToggle: (() -> Unit)? = null
): ListAdapter<PhotoPair, FullScreenImagePagerAdapter.ImageViewHolder>(DIFF) {

    companion object {

        private val DIFF = object: DiffUtil.ItemCallback<PhotoPair>() {
            override fun areItemsTheSame(a: PhotoPair, b: PhotoPair) = a.internalId == b.internalId
            override fun areContentsTheSame(a: PhotoPair, b: PhotoPair) =
                a.cleanedUri == b.cleanedUri && a.bgCleanStatus == b.bgCleanStatus
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemFullScreenImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ImageViewHolder(private val binding: ItemFullScreenImageBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pair: PhotoPair) {
            val uriToLoad = pair.cleanedUri ?: pair.originalUri
            binding.ivFullscreen.load(uriToLoad) {
                crossfade(true)
            }
            binding.ivFullscreen.setOnClickListener {
                onUiToggle?.invoke()
            }
        }
    }
}

