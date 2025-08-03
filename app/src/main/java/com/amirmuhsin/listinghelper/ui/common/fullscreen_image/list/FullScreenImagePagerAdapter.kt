package com.amirmuhsin.listinghelper.ui.common.fullscreen_image.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.amirmuhsin.listinghelper.databinding.ItemFullScreenImageBinding
import com.amirmuhsin.listinghelper.domain.model.PhotoPair

class FullScreenImagePagerAdapter(
    private val context: Context,
    private val items: List<PhotoPair>,
    private val onImageClick: ((PhotoPair) -> Unit)? = null
): RecyclerView.Adapter<FullScreenImagePagerAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemFullScreenImageBinding.inflate(LayoutInflater.from(context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ImageViewHolder(private val binding: ItemFullScreenImageBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pair: PhotoPair) {
            val uriToLoad = pair.cleanedUri ?: pair.originalUri
            binding.ivFullscreen.load(uriToLoad) {
                crossfade(true)
            }
            binding.ivFullscreen.setOnClickListener {
                onImageClick?.invoke(pair)
            }
        }
    }
}
